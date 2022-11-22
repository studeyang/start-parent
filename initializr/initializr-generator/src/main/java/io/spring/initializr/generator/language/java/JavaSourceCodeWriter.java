/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.generator.language.java;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.*;
import io.spring.initializr.generator.utils.Pair;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link SourceCodeWriter} that writes {@link SourceCode} in Java.
 *
 * @author Andy Wilkinson
 * @author Matt Berteaux
 */
public class JavaSourceCodeWriter implements SourceCodeWriter<JavaSourceCode> {

    private static final Map<Predicate<Integer>, String> TYPE_MODIFIERS;

    private static final Map<Predicate<Integer>, String> FIELD_MODIFIERS;

    private static final Map<Predicate<Integer>, String> METHOD_MODIFIERS;

    static {
        Map<Predicate<Integer>, String> typeModifiers = new LinkedHashMap<>();
        typeModifiers.put(Modifier::isPublic, "public");
        typeModifiers.put(Modifier::isProtected, "protected");
        typeModifiers.put(Modifier::isPrivate, "private");
        typeModifiers.put(Modifier::isAbstract, "abstract");
        typeModifiers.put(Modifier::isStatic, "static");
        typeModifiers.put(Modifier::isFinal, "final");
        typeModifiers.put(Modifier::isStrict, "strictfp");
        TYPE_MODIFIERS = typeModifiers;
        Map<Predicate<Integer>, String> fieldModifiers = new LinkedHashMap<>();
        fieldModifiers.put(Modifier::isPublic, "public");
        fieldModifiers.put(Modifier::isProtected, "protected");
        fieldModifiers.put(Modifier::isPrivate, "private");
        fieldModifiers.put(Modifier::isStatic, "static");
        fieldModifiers.put(Modifier::isFinal, "final");
        fieldModifiers.put(Modifier::isTransient, "transient");
        fieldModifiers.put(Modifier::isVolatile, "volatile");
        FIELD_MODIFIERS = fieldModifiers;
        Map<Predicate<Integer>, String> methodModifiers = new LinkedHashMap<>(typeModifiers);
        methodModifiers.put(Modifier::isSynchronized, "synchronized");
        methodModifiers.put(Modifier::isNative, "native");
        METHOD_MODIFIERS = methodModifiers;
    }

    private final IndentingWriterFactory indentingWriterFactory;

    public JavaSourceCodeWriter(IndentingWriterFactory indentingWriterFactory) {
        this.indentingWriterFactory = indentingWriterFactory;
    }

    @Override
    public void writeTo(SourceStructure structure, JavaSourceCode sourceCode) throws IOException {
        for (JavaCompilationUnit compilationUnit : sourceCode.getCompilationUnits()) {
            writeTo(structure, compilationUnit);
        }
    }

    private void writeTo(SourceStructure structure, JavaCompilationUnit compilationUnit) throws IOException {
        Path output = structure.createSourceFile(compilationUnit.getPackageName(), compilationUnit.getName());
        Files.createDirectories(output.getParent());
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("java",
                Files.newBufferedWriter(output))) {
            writer.println("package " + compilationUnit.getPackageName() + ";");
            writer.println();
            Set<String> imports = determineImports(compilationUnit);
            if (!imports.isEmpty()) {
                for (String importedType : imports) {
                    writer.println("import " + importedType + ";");
                }
                writer.println();
            }
            for (JavaTypeDeclaration type : compilationUnit.getTypeDeclarations()) {
                writeAnnotations(writer, type);
                writeModifiers(writer, TYPE_MODIFIERS, type.getModifiers());
                writer.print(compilationUnit.getClassType() + " " + type.getName());
                if (type.getExtends() != null) {
                    writer.print(" extends " + getUnqualifiedName(type.getExtends()));
                }
                if (type.getGenerics() != null) {
                    writer.print("<" + getUnqualifiedName(type.getGenerics()) + ">");
                }
                if (!type.getImplements().isEmpty()) {
                    writer.print(" implements " + type.getImplements().stream()
                            .map(this::getUnqualifiedName)
                            .collect(Collectors.joining(", ")));
                }
                writer.println(" {");
                writer.println();
                List<JavaFieldDeclaration> fieldDeclarations = type.getFieldDeclarations();
                if (!fieldDeclarations.isEmpty()) {
                    writer.indented(() -> {
                        for (JavaFieldDeclaration fieldDeclaration : fieldDeclarations) {
                            writeFieldDeclaration(writer, fieldDeclaration);
                        }
                    });
                }
                List<JavaMethodDeclaration> methodDeclarations = type.getMethodDeclarations();
                if (!methodDeclarations.isEmpty()) {
                    writer.indented(() -> {
                        for (JavaMethodDeclaration methodDeclaration : methodDeclarations) {
                            writeMethodDeclaration(writer, methodDeclaration, compilationUnit.isInterfaceType());
                        }
                    });
                }
                writer.println("}");
            }
        }
    }

    private void writeAnnotations(IndentingWriter writer, Annotatable annotatable) {
        annotatable.getAnnotations().forEach((annotation) -> writeAnnotation(writer, annotation));
    }

    private void writeParameterAnnotation(IndentingWriter writer, Annotation annotation) {
        writer.print("@" + getUnqualifiedName(annotation.getName()));
        List<Annotation.Attribute> attributes = annotation.getAttributes();
        if (!attributes.isEmpty()) {
            writer.print("(");
            if (attributes.size() == 1 && attributes.get(0).getName().equals("value")) {
                writer.print(formatAnnotationAttribute(attributes.get(0)));
            } else {
                writer.print(attributes.stream()
                        .map((attribute) -> attribute.getName() + " = " + formatAnnotationAttribute(attribute))
                        .collect(Collectors.joining(", ")));
            }
            writer.print(")");
        }
    }

    private void writeAnnotation(IndentingWriter writer, Annotation annotation) {
        writeParameterAnnotation(writer, annotation);
        writer.println();
    }

    private String formatAnnotationAttribute(Annotation.Attribute attribute) {
        List<String> values = attribute.getValues();
        if (attribute.getType().equals(Class.class)) {
            return formatValues(values, (value) -> String.format("%s.class", getUnqualifiedName(value)));
        }
        if (Enum.class.isAssignableFrom(attribute.getType())) {
            return formatValues(values, (value) -> {
                String enumValue = value.substring(value.lastIndexOf(".") + 1);
                String enumClass = value.substring(0, value.lastIndexOf("."));
                return String.format("%s.%s", getUnqualifiedName(enumClass), enumValue);
            });
        }
        if (attribute.getType().equals(String.class) || attribute.getType().equals(String[].class)) {
            return formatValues(values, (value) -> String.format("\"%s\"", value));
        }
        return formatValues(values, (value) -> String.format("%s", value));
    }

    private String formatValues(List<String> values, Function<String, String> formatter) {
        String result = values.stream().map(formatter).collect(Collectors.joining(", "));
        return (values.size() > 1) ? "{ " + result + " }" : result;
    }

    private void writeFieldDeclaration(IndentingWriter writer, JavaFieldDeclaration fieldDeclaration) {
        writeAnnotations(writer, fieldDeclaration);
        writeModifiers(writer, FIELD_MODIFIERS, fieldDeclaration.getModifiers());
        writer.print(getUnqualifiedName(fieldDeclaration.getReturnType()));
        writer.print(" ");
        writer.print(fieldDeclaration.getName());
        if (fieldDeclaration.isInitialized()) {
            writer.print(" = ");
            writer.print(String.valueOf(fieldDeclaration.getValue()));
        }
        writer.println(";");
        writer.println();
    }

    private void writeMethodDeclaration(IndentingWriter writer, JavaMethodDeclaration methodDeclaration,
                                        boolean isAbstractMethod) {
        writeAnnotations(writer, methodDeclaration);
        writeModifiers(writer, METHOD_MODIFIERS, methodDeclaration.getModifiers());
        writer.print(getUnqualifiedName(methodDeclaration.getReturnType()) + " " + methodDeclaration.getName() + "(");
        List<Parameter> parameters = methodDeclaration.getParameters();
        if (!parameters.isEmpty()) {
            writer.print(parameters.stream()
                    .flatMap(parameter -> {
                        if (parameter.getAnnotation() != null) {
                            writeParameterAnnotation(writer, parameter.getAnnotation());
                            writer.print(" ");
                        }
                        return Stream.of(parameter);
                    })
                    .map((parameter) -> getUnqualifiedName(parameter.getType()) + " " + parameter.getName())
                    .collect(Collectors.joining(", ")));
        }

        if (isAbstractMethod) {
            writer.println(");");
            return;
        }

        writer.println(") {");
        writer.indented(() -> {
            List<JavaStatement> statements = methodDeclaration.getStatements();
            for (JavaStatement statement : statements) {
                if (statement instanceof JavaExpressionStatement) {
                    writeExpression(writer, ((JavaExpressionStatement) statement).getExpression());
                } else if (statement instanceof JavaReturnStatement) {
                    writer.print("return ");
                    writeExpression(writer, ((JavaReturnStatement) statement).getExpression());
                } else if (statement instanceof JavaSimpleReturnStatement) {
                    String variable = ((JavaSimpleReturnStatement) statement).getVariable();
                    writer.print("return " + variable);
                }
                writer.println(";");
            }
        });
        writer.println("}");
        writer.println();
    }

    private void writeModifiers(IndentingWriter writer, Map<Predicate<Integer>, String> availableModifiers,
                                int declaredModifiers) {
        String modifiers = availableModifiers.entrySet().stream()
                .filter((entry) -> entry.getKey().test(declaredModifiers)).map(Entry::getValue)
                .collect(Collectors.joining(" "));
        if (!modifiers.isEmpty()) {
            writer.print(modifiers);
            writer.print(" ");
        }
    }

    private void writeExpression(IndentingWriter writer, JavaExpression expression) {
        if (expression instanceof JavaMethodInvocation) {
            writeMethodInvocation(writer, (JavaMethodInvocation) expression);
        } else if (expression instanceof JavaConstructorInvocation) {
            writeConstructorInvocation(writer, (JavaConstructorInvocation) expression);
        } else if (expression instanceof JavaStreamInvocation) {
            writeStreamInvocation(writer, (JavaStreamInvocation) expression);
        } else if (expression instanceof JavaStaticInvocation) {
            writeStaticInvocation(writer, (JavaStaticInvocation) expression);
        }
    }

    private void writeStreamInvocation(IndentingWriter writer, JavaStreamInvocation streamInvocation) {
        if (streamInvocation.getVariable() != null) {
            writer.print(variableToWrite(streamInvocation.getVariable()));
        }

        for (int i = 0; i < streamInvocation.getTargets().size(); i++) {
            JavaExpression target = streamInvocation.getTargets().get(i);
            if (target instanceof JavaConstructorInvocation) {
                writeConstructorInvocation(writer, (JavaConstructorInvocation) target);
                writer.println("");
            }
            if (target instanceof JavaMethodInvocation) {
                boolean notLast = i != streamInvocation.getTargets().size() - 1;
                writer.indented(() -> writer.indented(() -> {
                    writeMethodInvocation(writer, (JavaMethodInvocation) target);
                    if (notLast) {
                        writer.println("");
                    }
                }));
            }
        }
    }

    private void writeStaticInvocation(IndentingWriter writer, JavaStaticInvocation staticInvocation) {
        StringBuilder toWrite = new StringBuilder();
        if (staticInvocation.getVariable() != null) {
            toWrite.append(variableToWrite(staticInvocation.getVariable()));
        }
        toWrite.append(getUnqualifiedName(staticInvocation.getTarget())).append(".");
        Pair<Boolean, String> methodOrField = resolveMethodOrField(staticInvocation.getName());
        toWrite.append(methodOrField.getRight());

        boolean isMethod = methodOrField.getLeft();
        if (isMethod) {
            toWrite.append("(");
            for (String argument : staticInvocation.getArguments()) {
                toWrite.append(argument);
            }
            toWrite.append(")");
        }
        writer.print(toWrite.toString());
    }

    private void writeMethodInvocation(IndentingWriter writer, JavaMethodInvocation methodInvocation) {
        String toWrite = "";
        if (methodInvocation.getVariable() != null) {
            toWrite += variableToWrite(methodInvocation.getVariable());
        }

        if (methodInvocation.getTarget() != null) {
            toWrite += getUnqualifiedName(methodInvocation.getTarget());
        }

        toWrite += "." + methodInvocation.getName() + "(" +
                String.join(", ", methodInvocation.getArguments()) + ")";
        writer.print(toWrite);
    }

    private void writeConstructorInvocation(IndentingWriter writer, JavaConstructorInvocation constructorInvocation) {
        String toWrite = "";

        if (constructorInvocation.getVariable() != null) {
            toWrite += variableToWrite(constructorInvocation.getVariable());
        }

        toWrite += "new " + getUnqualifiedName(constructorInvocation.getTarget()) + "(" +
                String.join(", ", constructorInvocation.getArguments()) + ")";

        writer.print(toWrite);
    }

    private String variableToWrite(Variable variable) {
        String toWrite = getUnqualifiedName(variable.getType());
        if (variable.getGenerics() != null) {
            toWrite += "<" + getUnqualifiedName(variable.getGenerics()) + ">";
        }
        toWrite += " " + variable.getName() + " = ";
        return toWrite;
    }

    private Set<String> determineImports(JavaCompilationUnit compilationUnit) {
        List<String> imports = new ArrayList<>();
        for (JavaTypeDeclaration typeDeclaration : compilationUnit.getTypeDeclarations()) {
            if (requiresImport(typeDeclaration.getExtends())) {
                imports.add(typeDeclaration.getExtends());
            }
            if (requiresImport(typeDeclaration.getGenerics())) {
                imports.add(typeDeclaration.getGenerics());
            }
            // import OrderService in "public class OrderServiceImpl implements OrderService"
            imports.addAll(getRequiredImports(typeDeclaration.getImplements(),
                    Collections::singleton));
            imports.addAll(getRequiredImports(typeDeclaration.getAnnotations(), this::determineImports));
            for (JavaFieldDeclaration fieldDeclaration : typeDeclaration.getFieldDeclarations()) {
                if (requiresImport(fieldDeclaration.getReturnType())) {
                    imports.add(fieldDeclaration.getReturnType());
                }
                imports.addAll(getRequiredImports(fieldDeclaration.getAnnotations(), this::determineImports));
            }
            for (JavaMethodDeclaration methodDeclaration : typeDeclaration.getMethodDeclarations()) {
                if (requiresImport(methodDeclaration.getReturnType())) {
                    imports.add(methodDeclaration.getReturnType());
                }
                imports.addAll(getRequiredImports(methodDeclaration.getAnnotations(), this::determineImports));
                imports.addAll(getRequiredImports(methodDeclaration.getParameters(),
                        (parameter) -> Collections.singletonList(parameter.getType())));
                // import @Variable in "public void method(@Variable String id)"
                imports.addAll(getRequiredImports(methodDeclaration.getParameters().stream()
                                .map(Parameter::getAnnotation).filter(Objects::nonNull)
                                .map(Annotation::getName),
                        Collections::singletonList));
                imports.addAll(getRequiredImports(
                        getMethodInvocationStream(methodDeclaration),
                        (methodInvocation) -> Collections.singleton(methodInvocation.getTarget())));
                imports.addAll(getRequiredImports(
                        getMethodInvocationStream(methodDeclaration).map(JavaMethodInvocation::getVariable)
                                .filter(Objects::nonNull),
                        (variable) -> Collections.singleton(variable.getType())));
                // import Example in "Example example = new SubExample()"
                imports.addAll(getRequiredImports(
                        getJavaConstructorInvocationStream(methodDeclaration)
                                .map(JavaConstructorInvocation::getVariable).filter(Objects::nonNull),
                        variable -> Collections.singleton(variable.getType())));
                // import SubExample in "Example example = new SubExample()"
                imports.addAll(getRequiredImports(
                        getJavaConstructorInvocationStream(methodDeclaration)
                                .map(JavaConstructorInvocation::getTarget).filter(Objects::nonNull),
                        Collections::singleton));
                traverseJavaStaticInvocation(methodDeclaration, imports::add);
                traverseJavaStreamInvocation(methodDeclaration, imports::addAll);
            }
        }
        Collections.sort(imports);
        return new LinkedHashSet<>(imports);
    }

    private void traverseJavaStaticInvocation(JavaMethodDeclaration methodDeclaration, Consumer<String> addImports) {
        methodDeclaration.getStatements().stream()
                .filter(JavaExpressionStatement.class::isInstance).map(JavaExpressionStatement.class::cast)
                .map(JavaExpressionStatement::getExpression).filter(JavaStaticInvocation.class::isInstance)
                .map(JavaStaticInvocation.class::cast)
                .forEach(staticInvocation -> {
                    // import Example in "Example example = ..."
                    if (requiresImport(staticInvocation.getVariable().getType())) {
                        addImports.accept(staticInvocation.getVariable().getType());
                    }
                    // import Type in "Example<Type> example = ..."
                    if (requiresImport(staticInvocation.getVariable().getGenerics())) {
                        addImports.accept(staticInvocation.getVariable().getGenerics());
                    }
                    // import ExampleFactory in "Example example = ExampleFactory.get()"
                    if (requiresImport(staticInvocation.getTarget())) {
                        addImports.accept(staticInvocation.getTarget());
                    }
                });
    }

    private void traverseJavaStreamInvocation(JavaMethodDeclaration methodDeclaration, Consumer<List<String>> addImports) {
        Stream.concat(
                methodDeclaration.getStatements().stream()
                        .filter(JavaExpressionStatement.class::isInstance)
                        .map(JavaExpressionStatement.class::cast).map(JavaExpressionStatement::getExpression)
                        .filter(JavaStreamInvocation.class::isInstance)
                        .map(JavaStreamInvocation.class::cast).map(JavaStreamInvocation::getTargets),
                methodDeclaration.getStatements().stream()
                        .filter(JavaReturnStatement.class::isInstance)
                        .map(JavaReturnStatement.class::cast).map(JavaReturnStatement::getExpression)
                        .filter(JavaStreamInvocation.class::isInstance)
                        .map(JavaStreamInvocation.class::cast).map(JavaStreamInvocation::getTargets)
        ).forEach(targets -> {
            // import Example in "new Example().call()"
            addImports.accept(getRequiredImports(targets.stream()
                            .filter(JavaConstructorInvocation.class::isInstance)
                            .map(JavaConstructorInvocation.class::cast)
                            .map(JavaConstructorInvocation::getTarget),
                    Collections::singleton));
        });
    }

    private Stream<JavaMethodInvocation> getMethodInvocationStream(JavaMethodDeclaration methodDeclaration) {
        return methodDeclaration.getStatements().stream().filter(JavaExpressionStatement.class::isInstance)
                .map(JavaExpressionStatement.class::cast).map(JavaExpressionStatement::getExpression)
                .filter(JavaMethodInvocation.class::isInstance).map(JavaMethodInvocation.class::cast);
    }

    private Stream<JavaConstructorInvocation> getJavaConstructorInvocationStream(
            JavaMethodDeclaration methodDeclaration) {
        return methodDeclaration.getStatements().stream()
                .filter(JavaExpressionStatement.class::isInstance).map(JavaExpressionStatement.class::cast)
                .map(JavaExpressionStatement::getExpression).filter(JavaConstructorInvocation.class::isInstance)
                .map(JavaConstructorInvocation.class::cast);
    }

    private Collection<String> determineImports(Annotation annotation) {
        List<String> imports = new ArrayList<>();
        imports.add(annotation.getName());
        annotation.getAttributes().forEach((attribute) -> {
            if (attribute.getType() == Class.class) {
                imports.addAll(attribute.getValues());
            }
            if (Enum.class.isAssignableFrom(attribute.getType())) {
                imports.addAll(attribute.getValues().stream().map((value) -> value.substring(0, value.lastIndexOf(".")))
                        .collect(Collectors.toList()));
            }
        });
        return imports;
    }

    private <T> List<String> getRequiredImports(List<T> candidates, Function<T, Collection<String>> mapping) {
        return getRequiredImports(candidates.stream(), mapping);
    }

    private <T> List<String> getRequiredImports(Stream<T> candidates, Function<T, Collection<String>> mapping) {
        return candidates.map(mapping).flatMap(Collection::stream).filter(this::requiresImport)
                .collect(Collectors.toList());
    }

    private String getUnqualifiedName(String name) {
        if (!name.contains(".")) {
            return name;
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    private boolean requiresImport(String name) {
        if (name == null || !name.contains(".")) {
            return false;
        }
        String packageName = name.substring(0, name.lastIndexOf('.'));
        return !"java.lang".equals(packageName);
    }

    private Pair<Boolean, String> resolveMethodOrField(String name) {
        if (name.contains("()")) {
            return new Pair<>(true, name.replace("()", ""));
        } else {
            return new Pair<>(false, name);
        }

    }

}
