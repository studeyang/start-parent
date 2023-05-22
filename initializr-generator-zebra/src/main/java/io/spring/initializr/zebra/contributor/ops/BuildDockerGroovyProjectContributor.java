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
import io.spring.initializr.generator.ops.Dir;
import io.spring.initializr.generator.ops.DockerBuild;
import io.spring.initializr.generator.ops.OpsBuildWriter;
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
public class BuildDockerGroovyProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final DockerBuild dockerBuild;
    private final OpsBuildWriter opsBuildWriter;
    private final IndentingWriterFactory indentingWriterFactory;

    public BuildDockerGroovyProjectContributor(ProjectDescription description) {
        this.description = description;
        this.dockerBuild = new DockerBuild();
        this.opsBuildWriter = new OpsBuildWriter();
        this.indentingWriterFactory = IndentingWriterFactory.withDefaultSettings();
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path buildDocker = projectRoot.resolve("ops/buildDocker.groovy");
        if (Files.notExists(buildDocker)) {
            Files.createDirectories(buildDocker.getParent());
            Files.createFile(buildDocker);
        }

        customize();

        Writer out = Files.newBufferedWriter(buildDocker);
        try (IndentingWriter writer = this.indentingWriterFactory.createIndentingWriter("buildDocker", out)) {
            opsBuildWriter.writeTo(writer, dockerBuild);
        }
    }

    private void customize() {
        String serviceName = description.getArtifactId() + "-service";
        dockerBuild.setDir(
                Dir.newInstanceWithRunDockerBuild(serviceName, runDockerBuild -> {
                    runDockerBuild.add("appName", serviceName);
                    runDockerBuild.add("dockerRegistry", "swr.cn-south-1.myhuaweicloud.com");
                    runDockerBuild.add("dockerImage", "cassmall/" + serviceName);
                }));
    }

}
