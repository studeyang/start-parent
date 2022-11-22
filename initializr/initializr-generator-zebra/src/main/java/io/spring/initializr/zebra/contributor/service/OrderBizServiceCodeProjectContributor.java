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
import java.util.LinkedList;
import java.util.List;

import static io.spring.initializr.zebra.contributor.support.ContributorSupport.DEFAULT_CODE_CONTRIBUTOR_ORDER;
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;

/**
 * Service 模块 service/biz OrderBizService.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class OrderBizServiceCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public OrderBizServiceCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".service.biz", "OrderBizService");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("OrderBizService");
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
        javaTypeDeclaration.annotate(Annotation.name("org.springframework.stereotype.Service"));
        String packageName = this.description.getPackageName();

        List<JavaStatement> body = new LinkedList<>();

        if (description.getRequestedDependencies().containsKey("mybatis")) {
            // field
            JavaFieldDeclaration orderBizServiceField = JavaFieldDeclaration.field("orderMapper")
                    .modifiers(PRIVATE).returning(packageName + ".service.dao.mapper.OrderMapper");
            orderBizServiceField.annotate(Annotation.name("org.springframework.beans.factory.annotation.Autowired"));
            javaTypeDeclaration.addFieldDeclaration(orderBizServiceField);

            body.add(new JavaExpressionStatement(new JavaMethodInvocation(new Variable("int", "id"),
                    "java.lang.Integer", "parseInt", "orderId")));
            body.add(new JavaSimpleReturnStatement("orderMapper.selectById(id)"));
        } else {
            body.add(new JavaSimpleReturnStatement("new OrderEntity()"));
        }

        // selectById
        javaTypeDeclaration.addMethodDeclaration(
                JavaMethodDeclaration.method("selectById").modifiers(PUBLIC)
                        .returning(packageName + ".service.entity.OrderEntity")
                        .parameters(new Parameter("java.lang.String", "orderId"))
                        .body(body.toArray(new JavaStatement[0]))
        );
    }

}
