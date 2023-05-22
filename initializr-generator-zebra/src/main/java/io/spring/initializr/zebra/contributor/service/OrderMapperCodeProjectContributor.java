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
 * Service 模块 service/biz OrderMapper.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class OrderMapperCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public OrderMapperCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".service.dao.mapper", "OrderMapper");
        javaCompilationUnit.setClassType("interface");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("OrderMapper");
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

    public static String referenceClass(String packageName) {
        return packageName + ".service.dao.mapper.OrderMapper";
    }

    protected void customize(JavaTypeDeclaration javaTypeDeclaration) {
        javaTypeDeclaration.modifiers(PUBLIC);

        if (description.getRequestedDependencies().containsKey("mybatis-plus")) {
            javaTypeDeclaration.extend("com.baomidou.mybatisplus.core.mapper.BaseMapper", "OrderEntity");
        }

        if (description.getRequestedDependencies().containsKey("mybatis")) {
            javaTypeDeclaration.annotate(Annotation.name("org.apache.ibatis.annotations.Mapper"));
            javaTypeDeclaration.annotate(Annotation.name("org.springframework.stereotype.Repository"));
        }
        String packageName = this.description.getPackageName();

        // selectByStatus
        javaTypeDeclaration.addMethodDeclaration(
                JavaMethodDeclaration.method("selectById")
                        .returning(packageName + ".service.entity.OrderEntity")
                        .parameters(new Parameter("java.lang.Integer", "orderId"))
                        .body()
        );
    }

}
