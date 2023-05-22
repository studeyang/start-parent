package io.spring.initializr.zebra.autoconfig;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.condition.ConditionalOnProjectFormat;
import io.spring.initializr.generator.language.Annotation;
import io.spring.initializr.generator.language.java.JavaMethodDeclaration;
import io.spring.initializr.generator.language.java.JavaTypeDeclaration;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.zebra.ZebraProjectFormat;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.code.TestApplicationTypeCustomizer;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.zebra.customizer.build.ParentPomBuildCustomizer;
import io.spring.initializr.zebra.customizer.source.WebApplicationSourceCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
@ProjectGenerationConfiguration
@ConditionalOnProjectFormat(ZebraProjectFormat.ID)
public class ZebraProjectGenerationConfiguration {

    public static final String SPRINGBOOT_STARTER_ID = "root_starter";

    private final ProjectDescription description;

    public ZebraProjectGenerationConfiguration(ProjectDescription description) {
        this.description = description;
    }

    /* ======== Build Customizer ======== */
    @Bean
    public BuildCustomizer<Build> springBootStarterBuildCustomizer() {
        return (build) -> {
            build.dependencies().add(SPRINGBOOT_STARTER_ID,
                    Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter"));
            build.dependencies().add("starter-test",
                    Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-test")
                            .scope(DependencyScope.TEST_COMPILE));
        };
    }

    @Bean
    public ParentPomBuildCustomizer parentPomBuildCustomizer(InitializrMetadata metadata) {
        return new ParentPomBuildCustomizer(this.description, metadata);
    }

    /* ======== Source Customizer ======== */
    @Bean
    public WebApplicationSourceCustomizer webApplicationSourceCustomizer() {
        return new WebApplicationSourceCustomizer(this.description);
    }

    @Bean
    public TestApplicationTypeCustomizer<JavaTypeDeclaration> junitJupiterTestMethodContributor() {
        return (typeDeclaration) -> {
            JavaMethodDeclaration method = JavaMethodDeclaration.method("contextLoads")
                    .returning("void").body();
            method.annotate(Annotation.name("org.junit.Test"));
            typeDeclaration.addMethodDeclaration(method);
        };
    }


}
