package io.spring.initializr.zebra.contributor.service;

import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.language.java.*;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.zebra.contributor.support.ContributorSupport;

import java.io.IOException;
import java.nio.file.Path;

import static io.spring.initializr.zebra.contributor.support.ContributorOrder.DEFAULT_CODE_CONTRIBUTOR_ORDER;
import static java.lang.reflect.Modifier.PUBLIC;

/**
 * Service 模块 infrastructure/config MybatisConfig.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class MybatisConfigCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public MybatisConfigCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".infrastructure.config", "MybatisConfig");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("MybatisConfig ");
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
        javaTypeDeclaration.annotate(Annotation.name("org.springframework.transaction.annotation.EnableTransactionManagement"));
        javaTypeDeclaration.annotate(
                Annotation.name("org.mybatis.spring.annotation.MapperScan",
                        builder -> builder.attribute("value", String.class, description.getPackageName() + ".service.dao.mapper"))
        );

        // defaultTransactionManager
        JavaMethodDeclaration defaultTransactionManagerMethod = JavaMethodDeclaration.method("defaultTransactionManager")
                .modifiers(PUBLIC).parameters(new Parameter("javax.sql.DataSource", "dataSource"))
                .returning("org.springframework.jdbc.datasource.DataSourceTransactionManager")
                .body(
                        new JavaExpressionStatement(
                                new JavaConstructorInvocation(
                                        new Variable("org.springframework.jdbc.datasource.DataSourceTransactionManager", "transactionManager"),
                                        "DataSourceTransactionManager",
                                        "dataSource")
                        ),
                        new JavaExpressionStatement(
                                new JavaMethodInvocation(
                                        "transactionManager",
                                        "setGlobalRollbackOnParticipationFailure",
                                        "false")
                        ),
                        new JavaSimpleReturnStatement("transactionManager")
                );
        defaultTransactionManagerMethod.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
        javaTypeDeclaration.addMethodDeclaration(defaultTransactionManagerMethod);

        // paginationInterceptor
        if (description.getRequestedDependencies().containsKey("mybatis-plus")) {
            javaTypeDeclaration.addMethodDeclaration(paginationInterceptorMethod());
        }
    }

    private JavaMethodDeclaration paginationInterceptorMethod() {
        JavaMethodDeclaration paginationInterceptorMethod = JavaMethodDeclaration.method("paginationInterceptor")
                .modifiers(PUBLIC)
                .returning("com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor")
                .body(new JavaSimpleReturnStatement("new PaginationInterceptor()"));
        paginationInterceptorMethod.annotate(Annotation.name("org.springframework.context.annotation.Bean"));
        return paginationInterceptorMethod;
    }

}
