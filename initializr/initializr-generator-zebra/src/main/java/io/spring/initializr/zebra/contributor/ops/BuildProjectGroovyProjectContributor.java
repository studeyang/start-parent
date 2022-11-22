/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.zebra.contributor.ops;

import io.spring.initializr.generator.io.IndentingWriter;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.ops.Action;
import io.spring.initializr.generator.ops.Dir;
import io.spring.initializr.generator.ops.OpsBuildWriter;
import io.spring.initializr.generator.ops.ProjectBuild;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/15
 */
public class BuildProjectGroovyProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final ProjectBuild projectBuild;
    private final OpsBuildWriter opsBuildWriter;
    private final IndentingWriterFactory indentingWriterFactory;

    public BuildProjectGroovyProjectContributor(ProjectDescription description) {
        this.description = description;
        this.projectBuild = new ProjectBuild();
        this.opsBuildWriter = new OpsBuildWriter();
        this.indentingWriterFactory = IndentingWriterFactory.withDefaultSettings();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path buildProject = projectRoot.resolve("ops/buildProject.groovy");
        if (Files.notExists(buildProject)) {
            Files.createDirectories(buildProject.getParent());
            Files.createFile(buildProject);
        }

        customize();

        Writer out = Files.newBufferedWriter(buildProject);
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("buildProject", out)) {
            opsBuildWriter.writeTo(writer, projectBuild);
        }
    }

    private void customize() {
        String artifactId = description.getArtifactId();
        projectBuild.setAction(
                new Action("checkAndRunMaven", mvnActions -> {
                    mvnActions.add("clean");
                    mvnActions.add("-U");
                    mvnActions.add("deploy");
                    mvnActions.add("-N");
                })
        );
        projectBuild.addDir(
                Dir.newInstanceWithAction(artifactId + "-spi", action -> {
                    action.setName("checkAndRunMaven");
                    action.mvnAction("clean");
                    action.mvnAction("-U");
                    action.mvnAction("deploy");
                }));
        projectBuild.addDir(
                Dir.newInstanceWithAction(artifactId + "-service", action -> {
                    action.setName("runMaven");
                    action.mvnAction("clean");
                    action.mvnAction("install");
                }));
    }

}
