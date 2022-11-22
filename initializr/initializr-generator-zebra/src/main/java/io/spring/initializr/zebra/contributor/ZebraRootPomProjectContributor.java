package io.spring.initializr.zebra.contributor;

import io.spring.initializr.generator.buildsystem.BuildWriter;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuildWriter;
import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ZebraRootPomProjectContributor implements BuildWriter, ProjectContributor {

    private final MavenBuild build;

    private final IndentingWriterFactory indentingWriterFactory;

    private final MavenBuildWriter buildWriter;

    public ZebraRootPomProjectContributor(MavenBuild build, IndentingWriterFactory indentingWriterFactory) {
        this.build = build;
        this.indentingWriterFactory = indentingWriterFactory;
        this.buildWriter = new MavenBuildWriter();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path pomFile = Files.createFile(projectRoot.resolve("pom.xml"));
        writeBuild(Files.newBufferedWriter(pomFile));
    }

    @Override
    public void writeBuild(Writer out) throws IOException {
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("maven", out)) {
            this.buildWriter.writeRootPom(writer, this.build);
        }
    }

}
