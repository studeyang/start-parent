package io.spring.initializr.zebra.customizer.build;

import io.spring.initializr.generator.buildsystem.MavenRepository;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;

import static io.spring.initializr.zebra.autoconfig.ZebraProjectGenerationConfiguration.SPRINGBOOT_STARTER_ID;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/7/5
 */
public class ParentPomBuildCustomizer implements BuildCustomizer<MavenBuild> {

    private final ProjectDescription description;

    private final InitializrMetadata metadata;

    public ParentPomBuildCustomizer(ProjectDescription description, InitializrMetadata metadata) {
        this.description = description;
        this.metadata = metadata;
    }

    @Override
    public void customize(MavenBuild build) {

        // Override MetadataProjectDescriptionCustomizer # customize setApplicationName
        if (description instanceof MutableProjectDescription) {
            MutableProjectDescription mutableProjectDescription = (MutableProjectDescription) description;
            mutableProjectDescription.setApplicationName("WebApplication");
        }

        // Default
        build.settings().name(this.description.getName()).description(this.description.getDescription());
        build.properties().property("java.version", this.description.getLanguage().jvmVersion());
        build.properties().property("project.build.sourceEncoding", "UTF-8");
        build.properties().property("swagger2.version", "2.8.0");
        build.properties().property("spring-boot.version", description.getPlatformVersion().toString());

        InitializrConfiguration.Env.Maven maven = this.metadata.getConfiguration().getEnv().getMaven();
        String springBootVersion = this.description.getPlatformVersion().toString();
        InitializrConfiguration.Env.Maven.ParentPom parentPom = maven.resolveParentPom(springBootVersion);
        build.settings().parent(parentPom.getGroupId(), parentPom.getArtifactId(), parentPom.getVersion(),
                parentPom.getRelativePath());

        // Modules
        String artifactId = description.getArtifactId();
        build.settings().modules(artifactId + "-service", artifactId + "-spi");

        // dependencyManagement
        if (build.dependencies().has(SPRINGBOOT_STARTER_ID)) {
            build.boms().add("spring-boot");
        }

        // repositories
        build.repositories().add(
                MavenRepository.withIdAndUrl("cass-public",
                                "http://dev.yourcompany.com/nexus/content/groups/public/")
                        .name("Cass Public")
                        .snapshotsEnabled(true)
                        .build());

        // Build.plugins
        build.plugins().add("org.apache.maven.plugins", "maven-compiler-plugin", pluginBuilder -> {
            pluginBuilder.configuration(configurationBuilder -> {
                configurationBuilder.add("source", "${java.version}");
                configurationBuilder.add("target", "${java.version}");
                configurationBuilder.add("encoding", "${project.build.sourceEncoding}");
            });
        });
        build.plugins().add("org.apache.maven.plugins", "maven-source-plugin", pluginBuilder -> {
            pluginBuilder.execution(null, executionBuilder -> {
                executionBuilder.phase("compile");
                executionBuilder.goal("jar");
            });
        });
    }

    @Override
    public int getOrder() {
        return 4;
    }

}
