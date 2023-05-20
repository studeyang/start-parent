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
import static java.lang.reflect.Modifier.PRIVATE;
import static java.lang.reflect.Modifier.PUBLIC;

/**
 * Service 模块 application/service OrderAppService.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class OrderAppServiceCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public OrderAppServiceCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".application.service", "OrderAppService");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("OrderAppService");
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
        return packageName + ".application.service.OrderAppService";
    }

    private void customize(JavaTypeDeclaration javaTypeDeclaration) {
        javaTypeDeclaration.modifiers(PUBLIC);
        javaTypeDeclaration.annotate(Annotation.name("lombok.extern.slf4j.Slf4j"));
        javaTypeDeclaration.annotate(Annotation.name("org.springframework.stereotype.Service"));
        String packageName = this.description.getPackageName();

        // field
        JavaFieldDeclaration orderBizServiceField = JavaFieldDeclaration.field("orderBizService")
                .modifiers(PRIVATE).returning(packageName + ".service.biz.OrderBizService");
        orderBizServiceField.annotate(Annotation.name("org.springframework.beans.factory.annotation.Autowired"));
        javaTypeDeclaration.addFieldDeclaration(orderBizServiceField);

        // getOrderDetails
        javaTypeDeclaration.addMethodDeclaration(
                JavaMethodDeclaration.method("getOrderDetails").modifiers(PUBLIC)
                        .returning(packageName + ".dto.OrderDTO")
                        .parameters(new Parameter("java.lang.String", "orderId"))
                        .body(
                                new JavaExpressionStatement(
                                        new JavaMethodInvocation(
                                                new Variable(packageName + ".service.entity.OrderEntity", "order"),
                                                "orderBizService", "selectById", "orderId"
                                        )
                                ),
                                new JavaReturnStatement(
                                        new JavaStreamInvocation(new JavaConstructorInvocation("OrderDTO"))
                                                .target(JavaMethodInvocation.method("setOrderCreateDate", "order.getOrderCreateDate()"))
                                                .target(JavaMethodInvocation.method("setOrderId", "order.getId() + \"\""))
                                                .target(JavaMethodInvocation.method("setOrderStatus", "order.getOrderStatus() + \"\""))
                                                .target(JavaMethodInvocation.method("setOrderStatus", "order.getOrderStatus() + \"\""))
                                )
                        )
        );
    }

}
