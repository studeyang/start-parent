package io.spring.initializr.zebra.contributor.support;

import org.springframework.core.Ordered;

import java.nio.file.Path;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class ContributorSupport {

    public static Path getServicePath(Path projectRoot, String requestArtifactId) {
        return projectRoot.resolve(requestArtifactId + "-service");
    }

    public static Path getSpiPath(Path projectRoot, String requestArtifactId) {
        return projectRoot.resolve(requestArtifactId + "-spi");
    }

}
