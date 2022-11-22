package io.spring.initializr.zebra.contributor.spi;

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

import static io.spring.initializr.zebra.contributor.support.ContributorSupport.DEFAULT_CODE_CONTRIBUTOR_ORDER;
import static java.lang.reflect.Modifier.PUBLIC;

/**
 * Spi 模块 api OrderService.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class OrderServiceCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final JavaSourceCode javaSourceCode;
    private final JavaSourceCodeWriter javaSourceCodeWriter;

    public OrderServiceCodeProjectContributor(ProjectDescription description) {
        this.description = description;
        this.javaSourceCode = new JavaSourceCode();
        this.javaSourceCodeWriter = new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings());
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        JavaCompilationUnit javaCompilationUnit = javaSourceCode.createCompilationUnit(
                this.description.getPackageName() + ".api", "OrderService");
        javaCompilationUnit.setClassType("interface");
        JavaTypeDeclaration javaTypeDeclaration = javaCompilationUnit.createTypeDeclaration("OrderService");
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
        return packageName + ".api.OrderService";
    }

    private void customize(JavaTypeDeclaration javaTypeDeclaration) {
        javaTypeDeclaration.modifiers(PUBLIC);

        String packageName = this.description.getPackageName();

        // getById
        JavaMethodDeclaration getByIdMethod = JavaMethodDeclaration.method("getById")
                .returning(packageName + ".dto.OrderDTO")
                .parameters(new Parameter(
                        Annotation.name("org.springframework.web.bind.annotation.PathVariable",
                                builder -> builder.attribute("value", String.class, "orderId")),
                        "java.lang.String",
                        "orderId")
                )
                .body();
        getByIdMethod.annotate(Annotation.name("io.swagger.annotations.ApiOperation",
                builder -> builder.attribute("value", String.class, "查询订单信息"))
        );
        getByIdMethod.annotate(Annotation.name("org.springframework.web.bind.annotation.GetMapping",
                builder -> builder.attribute("value", String.class, "/order/{orderId}"))
        );
        javaTypeDeclaration.addMethodDeclaration(getByIdMethod);
    }

}
