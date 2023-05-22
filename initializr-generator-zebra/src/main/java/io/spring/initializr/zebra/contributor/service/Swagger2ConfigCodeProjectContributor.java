package io.spring.initializr.zebra.contributor.service;

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
 * Service 模块 infrastructure/config Swagger2Config.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class Swagger2ConfigCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public Swagger2ConfigCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".infrastructure.config", "Swagger2Config");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("Swagger2Config ");
        customize(javaTypeDeclaration);

        Path servicePath = ContributorSupport.getServicePath(projectRoot, description.getArtifactId());
        this.javaSourceCodeWriter.writeTo(
                new SourceStructure(servicePath.resolve("src/main/"), new JavaLanguage()),
                javaSourceCode);
    }

    @Override
    public int getOrder() {
        return DEFAULT_CODE_CONTRIBUTOR_ORDER;
    }

    private void customize(JavaTypeDeclaration javaTypeDeclaration) {
        javaTypeDeclaration.modifiers(PUBLIC);
        javaTypeDeclaration.annotate(Annotation.name("org.springframework.context.annotation.Configuration"));
        javaTypeDeclaration.annotate(Annotation.name("springfox.documentation.swagger2.annotations.EnableSwagger2"));
        String service = description.getArtifactId() + "-service";
        String version = description.getVersion();

        // createRestApi
        JavaMethodDeclaration createRestApiMethod = getCreateRestApiMethod();
        createRestApiMethod.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
        javaTypeDeclaration.addMethodDeclaration(createRestApiMethod);
        // apiInfo
        javaTypeDeclaration.addMethodDeclaration(
                JavaMethodDeclaration.method("apiInfo").modifiers(PRIVATE)
                        .returning("springfox.documentation.service.ApiInfo")
                        .body(
                                new JavaExpressionStatement(
                                        new JavaConstructorInvocation(
                                                new Variable("springfox.documentation.service.Contact", "contact"),
                                                "springfox.documentation.service.Contact", "\"\"", "\"\"", "\"\"")
                                ),
                                new JavaReturnStatement(
                                        new JavaStreamInvocation(new JavaConstructorInvocation("springfox.documentation.builders.ApiInfoBuilder"))
                                                .target(JavaMethodInvocation.method("title", "\"" + service + "\""))
                                                .target(JavaMethodInvocation.method("description", "\"API 接口说明[" + service + "]\""))
                                                .target(JavaMethodInvocation.method("version", "\"" + version + "\""))
                                                .target(JavaMethodInvocation.method("contact", "contact"))
                                                .target(JavaMethodInvocation.method("build"))
                                )
                        )
        );
    }

    private JavaMethodDeclaration getCreateRestApiMethod() {
        return JavaMethodDeclaration.method("createRestApi").modifiers(PUBLIC)
                .returning("springfox.documentation.spring.web.plugins.Docket")
                .body(
                        new JavaExpressionStatement(
                                new JavaStaticInvocation(
                                        new Variable("springfox.documentation.spi.DocumentationType", "type"),
                                        "DocumentationType",
                                        "SWAGGER_2")
                        ),
                        new JavaExpressionStatement(
                                new JavaStaticInvocation(
                                        new Variable("Class<RestController>", "restControllerClass"),
                                        "org.springframework.web.bind.annotation.RestController",
                                        "class")
                        ),
                        new JavaExpressionStatement(
                                new JavaStaticInvocation(
                                        new Variable("com.google.common.base.Predicate", "springfox.documentation.RequestHandler", "selector"),
                                        "springfox.documentation.builders.RequestHandlerSelectors",
                                        "withClassAnnotation()",
                                        "restControllerClass")
                        ),
                        new JavaExpressionStatement(
                                new JavaStaticInvocation(
                                        new Variable("com.google.common.base.Predicate", "java.lang.String", "apis"),
                                        "springfox.documentation.builders.PathSelectors",
                                        "any()")
                        ),
                        new JavaReturnStatement(
                                new JavaStreamInvocation(new JavaConstructorInvocation("Docket", "type"))
                                        .target(JavaMethodInvocation.method("enableUrlTemplating", "true"))
                                        .target(JavaMethodInvocation.method("groupName", "\"\""))
                                        .target(JavaMethodInvocation.method("apiInfo", "apiInfo()"))
                                        .target(JavaMethodInvocation.method("select"))
                                        .target(JavaMethodInvocation.method("apis", "selector"))
                                        .target(JavaMethodInvocation.method("paths", "apis"))
                                        .target(JavaMethodInvocation.method("build"))
                        )
                );
    }

}
