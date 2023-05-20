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
 * Service 模块 restful OrderServiceImpl.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class OrderServiceImplCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public OrderServiceImplCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".restful", "OrderServiceImpl");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("OrderServiceImpl");
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
        javaTypeDeclaration.annotate(Annotation.name("lombok.extern.slf4j.Slf4j"));
        javaTypeDeclaration.annotate(Annotation.name("org.springframework.web.bind.annotation.RestController"));
        String packageName = this.description.getPackageName();
        javaTypeDeclaration.implement(packageName + ".api.OrderService");

        // field
        JavaFieldDeclaration orderDetailsAppServiceField = JavaFieldDeclaration.field("orderDetailsAppService")
                .modifiers(PRIVATE).returning(OrderAppServiceCodeProjectContributor.referenceClass(packageName));
        orderDetailsAppServiceField.annotate(Annotation.name("org.springframework.beans.factory.annotation.Autowired"));
        javaTypeDeclaration.addFieldDeclaration(orderDetailsAppServiceField);

        // getById
        JavaMethodDeclaration getByIdMehtod = JavaMethodDeclaration.method("getById").modifiers(PUBLIC)
                .returning(packageName + ".dto.OrderDTO")
                .parameters(new Parameter(Annotation.name("org.springframework.web.bind.annotation.PathVariable"),
                        "java.lang.String", "orderId"))
                .body(
                        new JavaSimpleReturnStatement("orderDetailsAppService.getOrderDetails(orderId)")
                );
        getByIdMehtod.annotate(Annotation.name("java.lang.Override"));
        javaTypeDeclaration.addMethodDeclaration(getByIdMehtod);
    }

}
