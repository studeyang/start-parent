package io.spring.initializr.zebra.contributor.support;

import org.springframework.core.Ordered;

import java.nio.file.Path;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/9
 */
public class ContributorSupport {

    public static final int SERVICE_STRUCTURE_CONTRIBUTOR_ORDER = Ordered.HIGHEST_PRECEDENCE;
    public static final int WEB_APPLICATION_CODE_CONTRIBUTOR_ORDER = after(SERVICE_STRUCTURE_CONTRIBUTOR_ORDER);
    public static final int DEFAULT_CODE_CONTRIBUTOR_ORDER = after(WEB_APPLICATION_CODE_CONTRIBUTOR_ORDER);

    private static int after(int order) {
        return order + 1;
    }

    public static Path getServicePath(Path projectRoot, String requestArtifactId) {
        return projectRoot.resolve(requestArtifactId + "-service");
    }

    public static Path getSpiPath(Path projectRoot, String requestArtifactId) {
        return projectRoot.resolve(requestArtifactId + "-spi");
    }

}
