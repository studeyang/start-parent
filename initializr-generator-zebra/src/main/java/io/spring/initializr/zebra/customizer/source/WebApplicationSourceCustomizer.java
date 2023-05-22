package io.spring.initializr.zebra.customizer.source;

import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.Parameter;
import io.spring.initializr.generator.language.java.JavaExpressionStatement;
import io.spring.initializr.generator.language.java.JavaMethodDeclaration;
import io.spring.initializr.generator.language.java.JavaMethodInvocation;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.code.MainApplicationTypeCustomizer;
import io.spring.initializr.generator.spring.code.SourceCodeProjectGenerationConfiguration;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public class WebApplicationSourceCustomizer implements MainApplicationTypeCustomizer<JavaTypeDeclaration> {

    private final ProjectDescription description;

    public WebApplicationSourceCustomizer(ProjectDescription description) {
        this.description = description;
    }

    /**
     * 这里已经添加了 @SpringBootApplication：
     * {@link SourceCodeProjectGenerationConfiguration#springBootApplicationAnnotator()}
     * */
    @Override
    public void customize(JavaTypeDeclaration typeDeclaration) {

        List<String> commons = new LinkedList<>();
        if (description.getRequestedDependencies().containsKey("mysql")) {
            commons.add("__common_database_");
        }
        if (description.getRequestedDependencies().containsKey("cloud-eureka")) {
            typeDeclaration.annotate(Annotation.name("org.springframework.cloud.client.discovery.EnableDiscoveryClient"));
            commons.add("__common_eureka_");
        }
        if (description.getRequestedDependencies().containsKey("starter-feign")) {
            typeDeclaration.annotate(Annotation.name("org.springframework.cloud.netflix.feign.EnableFeignClients"));
        }
        if (description.getRequestedDependencies().containsKey("commons") && !commons.isEmpty()) {
            typeDeclaration.annotate(Annotation.name(
                    "com.yourcompany.commons.custom.spring.cloud.config.PrepareConfigurations",
                    annotationBuilder -> annotationBuilder.attribute("value", String[].class, commons.toArray(new String[0])))
            );
        }

        typeDeclaration.modifiers(Modifier.PUBLIC);
        typeDeclaration.addMethodDeclaration(
                JavaMethodDeclaration.method("main").modifiers(Modifier.PUBLIC | Modifier.STATIC).returning("void")
                        .parameters(new Parameter("java.lang.String[]", "args"))
                        .body(
                                new JavaExpressionStatement(
                                        new JavaMethodInvocation("org.springframework.boot.SpringApplication", "run", typeDeclaration.getName() + ".class", "args")
                                )
                        )
        );
    }

}
