package io.spring.initializr.zebra.contributor.spi;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.*;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.zebra.contributor.support.ContributorSupport;

import java.io.IOException;
import java.nio.file.Path;

import static io.spring.initializr.zebra.contributor.support.ContributorOrder.DEFAULT_CODE_CONTRIBUTOR_ORDER;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;

/**
 * Spi 模块 request ModifyOrderRequest.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class ModifyOrderRequestCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public ModifyOrderRequestCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".request", "ModifyOrderRequest");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("ModifyOrderRequest");
        customize(javaTypeDeclaration);

        Path servicePath = ContributorSupport.getSpiPath(projectRoot, description.getArtifactId());
        this.javaSourceCodeWriter.writeTo(
                new SourceStructure(servicePath.resolve("src/main/"), new JavaLanguage()),
                javaSourceCode);
    }

    @Override
    public int getOrder() {
        return DEFAULT_CODE_CONTRIBUTOR_ORDER;
    }

    public static String referenceClass(String packageName) {
        return packageName + ".request.ModifyOrderRequest";
    }

    private void customize(JavaTypeDeclaration javaTypeDeclaration) {
        javaTypeDeclaration.modifiers(PUBLIC);

        javaTypeDeclaration.annotate(Annotation.name("lombok.Data"));
        javaTypeDeclaration.annotate(Annotation.name("lombok.experimental.Accessors",
                builder -> builder.attribute("chain", Boolean.class, "true")));
        javaTypeDeclaration.annotate(Annotation.name("io.swagger.annotations.ApiModel",
                builder -> builder.attribute("description", String.class, "订单修改请求")));

        // field
        JavaFieldDeclaration orderIdField = JavaFieldDeclaration.field("orderStatus").modifiers(PRIVATE)
                .returning("java.lang.Integer");
        orderIdField.annotate(Annotation.name("io.swagger.annotations.ApiModelProperty",
                builder -> builder.attribute("value", String.class, "订单状态")));

        JavaFieldDeclaration orderStatusField = JavaFieldDeclaration.field("lastUpdatedBy").modifiers(PRIVATE)
                .returning("java.lang.String");
        orderStatusField.annotate(Annotation.name("io.swagger.annotations.ApiModelProperty",
                builder -> builder.attribute("value", String.class, "修改人")));

        JavaFieldDeclaration orderCreateDateField = JavaFieldDeclaration.field("id").modifiers(PRIVATE)
                .returning("java.lang.Integer");
        orderCreateDateField.annotate(Annotation.name("io.swagger.annotations.ApiModelProperty",
                builder -> builder.attribute("value", String.class, "订单创建时间")));

        javaTypeDeclaration.addFieldDeclaration(orderIdField);
        javaTypeDeclaration.addFieldDeclaration(orderStatusField);
        javaTypeDeclaration.addFieldDeclaration(orderCreateDateField);

    }

}
