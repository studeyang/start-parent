package io.spring.initializr.zebra.contributor.service;

import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.spring.code.MainSourceCodeProjectContributor;
import io.spring.initializr.generator.spring.code.TestSourceCodeProjectContributor;
import io.spring.initializr.zebra.contributor.support.ContributorOrder;
import io.spring.initializr.zebra.contributor.support.ContributorSupport;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Service 模块 WebApplication.java 生成
 *
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class WebApplicationCodeProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final MainSourceCodeProjectContributor mainSourceCodeProjectContributor;
    private final TestSourceCodeProjectContributor testSourceCodeProjectContributor;

    public WebApplicationCodeProjectContributor(ProjectDescription description,
                                                MainSourceCodeProjectContributor mainSourceCodeProjectContributor,
                                                TestSourceCodeProjectContributor testSourceCodeProjectContributor) {
        this.description = description;
        this.mainSourceCodeProjectContributor = mainSourceCodeProjectContributor;
        this.testSourceCodeProjectContributor = testSourceCodeProjectContributor;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path servicePath = ContributorSupport.getServicePath(projectRoot, description.getArtifactId());

        // WebApplication.java & WebApplicationTest.java
        mainSourceCodeProjectContributor.contribute(servicePath);
        testSourceCodeProjectContributor.contribute(servicePath);
    }

    @Override
    public int getOrder() {
        return ContributorOrder.WebApplicationCodeProjectContributor;
    }

}
