package io.spring.initializr.zebra.contributor.service;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.version.VersionReference;
import io.spring.initializr.zebra.contributor.support.ContributorOrder;
import io.spring.initializr.zebra.contributor.support.ContributorSupport;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Service 模块基础结构生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class ZebraServiceStructureProjectContributor implements BuildWriter, ProjectContributor {

    private final ProjectDescription description;
    private final MavenBuild build;
    private final IndentingWriterFactory indentingWriterFactory;
    private final MavenBuildWriter buildWriter;

    public ZebraServiceStructureProjectContributor(ProjectDescription description,
                                                   MavenBuild build,
                                                   IndentingWriterFactory indentingWriterFactory) {
        this.description = description;
        this.build = copy(build);
        this.indentingWriterFactory = indentingWriterFactory;
        this.buildWriter = new MavenBuildWriter();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path servicePath = ContributorSupport.getServicePath(projectRoot, description.getArtifactId());

        // 4 layer
        String packageDir = description.getPackageName().replace(".", "/");
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/application/eventhandler"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/application/jobhandler"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/application/service"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/application/validator"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/infrastructure/config"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/infrastructure/feign"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/restful"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/service/biz"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/service/dao"));
        createDirectories(servicePath.resolve("src/main/java/" + packageDir + "/service/entity"));

        // pom.xml
        Path pomFile = Files.createFile(servicePath.resolve("pom.xml"));
        writeBuild(Files.newBufferedWriter(pomFile));
    }

    @Override
    public int getOrder() {
        return ContributorOrder.ZebraServiceStructureProjectContributor;
    }

    @Override
    public void writeBuild(Writer out) throws IOException {
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("maven", out)) {
            this.buildWriter.writeTo(writer, this.build);
        }
    }

    private void createDirectories(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    private MavenBuild copy(MavenBuild source) {
        MavenBuild mavenBuild = new MavenBuild();
        // base
        mavenBuild.settings().artifact(source.getSettings().getArtifact() + "-service");
        mavenBuild.settings().version(source.getSettings().getVersion());
        mavenBuild.settings().name(source.getSettings().getName() + " service");
        mavenBuild.settings().packaging("jar");

        // parent
        mavenBuild.settings().parent(source.getSettings().getGroup(), source.getSettings().getArtifact(),
                source.getSettings().getVersion(), "../pom.xml");

        // properties
        mavenBuild.properties().property("docker.imageName", "${project.artifactId}");
        mavenBuild.properties().property("docker.imageVersion", "${project.version}");
        mavenBuild.properties().property("docker.plugin.version", "1.0.0");
        mavenBuild.properties().property("dockerfile.plugin.version", "1.4.0");
        mavenBuild.properties().property("docker.registry", "swr.cn-south-1.myhuaweicloud.com/cassmall");

        // dependencies
        mavenBuild.dependencies().add("lombok",
                Dependency.withCoordinates("org.projectlombok", "lombok")
                        .scope(DependencyScope.PROVIDED_RUNTIME));
        mavenBuild.dependencies().add("spi",
                Dependency.withCoordinates(description.getGroupId(), description.getArtifactId() + "-spi")
                        .version(VersionReference.ofValue("${project.version}")));
        if (description.getRequestedDependencies().containsKey("casslog")) {
            mavenBuild.dependencies().add("logging",
                    Dependency.withCoordinates("org.springframework.boot", "spring-boot-starter-logging")
                            .exclusions(new Dependency.Exclusion("*", "*")));
        }
        source.dependencies().ids().forEach(id -> mavenBuild.dependencies().add(id, source.dependencies().get(id)));

        // build
        mavenBuild.settings().finalName(description.getArtifactId() + "-service");
        mavenBuild.resources().add("src/main/resources", resourceBuilder -> {
            resourceBuilder.includes("**");
            resourceBuilder.filtering(false);
        });
        mavenBuild.resources().add("src/main/java", resourceBuilder -> {
            resourceBuilder.includes("**");
            resourceBuilder.filtering(false);
        });
        mavenBuild.plugins().add("org.springframework.boot", "spring-boot-maven-plugin", pluginBuilder -> {
            pluginBuilder.execution(null, executionBuilder -> executionBuilder.goal("repackage"));
        });
        mavenBuild.plugins().add("com.spotify", "dockerfile-maven-plugin", pluginBuilder -> {
            pluginBuilder.version("${dockerfile.plugin.version}");
            pluginBuilder.configuration(configurationBuilder -> {
                configurationBuilder.add("repository", "${docker.registry}/${docker.imageName}");
                configurationBuilder.add("useMavenSettingsForAuth", "true");
                configurationBuilder.add("tag", "${docker.imageVersion}");
                configurationBuilder.add("buildArgs", builder ->
                        builder.add("JAR_FILE", "target/${project.build.finalName}.jar"));
            });
        });

        return mavenBuild;
    }

}
