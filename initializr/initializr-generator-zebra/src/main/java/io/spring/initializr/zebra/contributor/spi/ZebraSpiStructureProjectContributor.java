package io.spring.initializr.zebra.contributor.spi;

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
import io.spring.initializr.zebra.contributor.support.ContributorSupport;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZebraSpiStructureProjectContributor implements BuildWriter, ProjectContributor {

    private final ProjectDescription description;
    private final MavenBuild build;
    private final IndentingWriterFactory indentingWriterFactory;
    private final MavenBuildWriter buildWriter;

    public ZebraSpiStructureProjectContributor(ProjectDescription description,
                                               MavenBuild build,
                                               IndentingWriterFactory indentingWriterFactory) {
        this.description = description;
        this.build = copy(build);
        this.indentingWriterFactory = indentingWriterFactory;
        this.buildWriter = new MavenBuildWriter();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path spiPath = ContributorSupport.getSpiPath(projectRoot, description.getArtifactId());

        // layer
        String packageDir = description.getPackageName().replace(".", "/");
        createDirectories(spiPath.resolve("src/main/java/" + packageDir + "/api"));
        createDirectories(spiPath.resolve("src/main/java/" + packageDir + "/dto"));
        createDirectories(spiPath.resolve("src/main/java/" + packageDir + "/request"));

        // pom.xml
        Path pomFile = Files.createFile(spiPath.resolve("pom.xml"));
        writeBuild(Files.newBufferedWriter(pomFile));
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
        mavenBuild.settings().artifact(source.getSettings().getArtifact() + "-spi");
        mavenBuild.settings().version(source.getSettings().getVersion());
        mavenBuild.settings().name(source.getSettings().getName() + " spi");
        mavenBuild.settings().packaging("jar");

        // parent
        mavenBuild.settings().parent(source.getSettings().getGroup(), source.getSettings().getArtifact(),
                source.getSettings().getVersion(), "../pom.xml");

        // dependencies
        mavenBuild.dependencies().add("lombok", "org.projectlombok", "lombok",
                DependencyScope.PROVIDED_RUNTIME);

        if (description.getRequestedDependencies().containsKey("web")) {
            mavenBuild.dependencies().add("spring-web",
                    Dependency.withCoordinates("org.springframework", "spring-web")
                            .build());
            mavenBuild.dependencies().add("swagger2",
                    Dependency.withCoordinates("io.springfox", "springfox-swagger2")
                            .version(VersionReference.ofValue("${swagger2.version}"))
                            .build());
            mavenBuild.dependencies().add("swagger-ui",
                    Dependency.withCoordinates("io.springfox", "springfox-swagger-ui")
                            .version(VersionReference.ofValue("${swagger2.version}"))
                            .build());
        }

        return mavenBuild;
    }

}
