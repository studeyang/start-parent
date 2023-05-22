package io.spring.initializr.zebra.autoconfig;

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.condition.ConditionalOnProjectFormat;
import io.spring.initializr.generator.condition.ConditionalOnRequestedDependency;
import io.spring.initializr.generator.condition.ConditionalOnRequestedDependencyAnyMatch;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.language.java.JavaSourceCode;
import io.spring.initializr.generator.language.java.JavaSourceCodeWriter;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.project.zebra.ZebraProjectFormat;
import io.spring.initializr.generator.spring.code.*;
import io.spring.initializr.zebra.contributor.ZebraRootPomProjectContributor;
import io.spring.initializr.zebra.contributor.ops.BuildDockerGroovyProjectContributor;
import io.spring.initializr.zebra.contributor.ops.BuildProjectGroovyProjectContributor;
import io.spring.initializr.zebra.contributor.service.*;
import io.spring.initializr.zebra.contributor.spi.ModifyOrderRequestCodeProjectContributor;
import io.spring.initializr.zebra.contributor.spi.OrderDtoCodeProjectContributor;
import io.spring.initializr.zebra.contributor.spi.OrderServiceCodeProjectContributor;
import io.spring.initializr.zebra.contributor.spi.ZebraSpiStructureProjectContributor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/12
 */
@ProjectGenerationConfiguration
@ConditionalOnProjectFormat(ZebraProjectFormat.ID)
public class ProjectContributorAutoConfiguration {

    private final ProjectDescription description;

    public ProjectContributorAutoConfiguration(ProjectDescription description) {
        this.description = description;
    }

    /* ======== service ======== */

    @Bean
    public ZebraRootPomProjectContributor zebraRootPomProjectContributor(
            MavenBuild build,
            IndentingWriterFactory indentingWriterFactory) {
        return new ZebraRootPomProjectContributor(build, indentingWriterFactory);
    }

    @Bean
    public ApplicationYmlProjectContributor bootstrapYmlProjectContributor(ProjectDescription description) {
        return new ApplicationYmlProjectContributor(description);
    }

    @Bean
    public ZebraServiceStructureProjectContributor zebraServiceProjectContributor(
            MavenBuild build,
            IndentingWriterFactory indentingWriterFactory) {
        return new ZebraServiceStructureProjectContributor(this.description, build, indentingWriterFactory);
    }

    @Bean
    public WebApplicationCodeProjectContributor webApplicationCodeProjectContributor(
            ObjectProvider<MainApplicationTypeCustomizer<?>> mainApplicationTypeCustomizers,
            ObjectProvider<MainCompilationUnitCustomizer<?, ?>> mainCompilationUnitCustomizers,
            ObjectProvider<MainSourceCodeCustomizer<?, ?, ?>> mainSourceCodeCustomizers,
            ObjectProvider<TestApplicationTypeCustomizer<?>> testApplicationTypeCustomizers,
            ObjectProvider<TestSourceCodeCustomizer<?, ?, ?>> testSourceCodeCustomizers) {
        return new WebApplicationCodeProjectContributor(
                this.description,
                new MainSourceCodeProjectContributor<>(
                        this.description,
                        JavaSourceCode::new,
                        new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings()),
                        mainApplicationTypeCustomizers,
                        mainCompilationUnitCustomizers,
                        mainSourceCodeCustomizers),
                new TestSourceCodeProjectContributor<>(
                        this.description,
                        JavaSourceCode::new,
                        new JavaSourceCodeWriter(IndentingWriterFactory.withDefaultSettings()),
                        testApplicationTypeCustomizers,
                        testSourceCodeCustomizers)
        );
    }

    @Bean
    @ConditionalOnRequestedDependency("web")
    public OrderServiceImplCodeProjectContributor orderServiceImplCodeProjectContributor() {
        return new OrderServiceImplCodeProjectContributor(this.description);
    }

    @Bean
    public OrderAppServiceCodeProjectContributor orderAppServiceCodeProjectContributor() {
        return new OrderAppServiceCodeProjectContributor(this.description);
    }

    @Bean
    public OrderBizServiceCodeProjectContributor orderBizServiceCodeProjectContributor() {
        return new OrderBizServiceCodeProjectContributor(this.description);
    }

    @Bean
    @ConditionalOnRequestedDependencyAnyMatch({"mybatis", "mybatis-plus"})
    public OrderMapperCodeProjectContributor orderMapperCodeProjectContributor() {
        return new OrderMapperCodeProjectContributor(this.description);
    }

    @Bean
    @ConditionalOnRequestedDependencyAnyMatch({"mybatis", "mybatis-plus"})
    public OrderMapperXmlProjectContributor orderMapperXmlProjectContributor() {
        return new OrderMapperXmlProjectContributor(this.description);
    }

    @Bean
    public OrderEntityCodeProjectContributor orderEntityCodeProjectContributor() {
        return new OrderEntityCodeProjectContributor(this.description);
    }

    @Bean
    @ConditionalOnRequestedDependency("web")
    public Swagger2ConfigCodeProjectContributor swagger2ConfigCodeProjectContributor() {
        return new Swagger2ConfigCodeProjectContributor(this.description);
    }

    @Bean
    @ConditionalOnRequestedDependencyAnyMatch({"mybatis", "mybatis-plus"})
    public MybatisConfigCodeProjectContributor mybatisConfigCodeProjectContributor() {
        return new MybatisConfigCodeProjectContributor(this.description);
    }

    @Bean
    public DockerfileProjectContributor dockerfileProjectContributor() {
        return new DockerfileProjectContributor(this.description);
    }

    /* ======== spi ======== */

    @Bean
    public ZebraSpiStructureProjectContributor zebraSpiStructureProjectContributor(
            MavenBuild build,
            IndentingWriterFactory indentingWriterFactory) {
        return new ZebraSpiStructureProjectContributor(this.description, build, indentingWriterFactory);
    }

    @Bean
    @ConditionalOnRequestedDependency("web")
    public OrderServiceCodeProjectContributor orderServiceCodeProjectContributor() {
        return new OrderServiceCodeProjectContributor(this.description);
    }

    @Bean
    public OrderDtoCodeProjectContributor orderDtoCodeProjectContributor() {
        return new OrderDtoCodeProjectContributor(this.description);
    }

    @Bean
    public ModifyOrderRequestCodeProjectContributor modifyOrderRequestCodeProjectContributor() {
        return new ModifyOrderRequestCodeProjectContributor(this.description);
    }

    /* ======== ops ======== */

    @Bean
    public BuildDockerGroovyProjectContributor buildDockerGroovyProjectContributor() {
        return new BuildDockerGroovyProjectContributor(this.description);
    }

    @Bean
    public BuildProjectGroovyProjectContributor buildProjectGroovyProjectContributor() {
        return new BuildProjectGroovyProjectContributor(this.description);
    }

}
