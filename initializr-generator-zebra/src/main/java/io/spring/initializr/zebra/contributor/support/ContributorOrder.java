package io.spring.initializr.zebra.contributor.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.Ordered;

/**
 * @since 1.0 2023/5/20
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContributorOrder {

    public static final int ZebraServiceStructureProjectContributor = Ordered.HIGHEST_PRECEDENCE;
    public static final int WebApplicationCodeProjectContributor = after(ZebraServiceStructureProjectContributor);
    public static final int DEFAULT_CODE_CONTRIBUTOR_ORDER = after(WebApplicationCodeProjectContributor);

    public static final int MavenBuildProjectContributor = 0;
    public static final int ZebraRootPomProjectContributor = after(MavenBuildProjectContributor);

    private static int after(int order) {
        return order + 1;
    }

}
