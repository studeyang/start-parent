package io.spring.initializr.zebra.contributor.service;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.project.contributor.SingleResourceProjectContributor;

import java.io.IOException;
import java.nio.file.Path;

import static io.spring.initializr.zebra.contributor.support.ContributorOrder.DEFAULT_CODE_CONTRIBUTOR_ORDER;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/18
 */
public class DockerfileProjectContributor implements ProjectContributor {

    private final SingleResourceProjectContributor singleResourceProjectContributor;

    public DockerfileProjectContributor(ProjectDescription description) {
        String servicePath = description.getArtifactId() + "-service";
        this.singleResourceProjectContributor = new SingleResourceProjectContributor(
                servicePath + "/Dockerfile",
                "classpath:configuration/Dockerfile");
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        singleResourceProjectContributor.contribute(projectRoot);
    }

    @Override
    public int getOrder() {
        return DEFAULT_CODE_CONTRIBUTOR_ORDER;
    }

}
