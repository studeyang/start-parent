package io.spring.start.site.extension.dependency.casslog;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenDependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/6/30
 */
public class CasslogMavenBuildCustomezer implements BuildCustomizer<MavenBuild> {

    @Override
    public void customize(MavenBuild build) {
        Dependency casslog = build.dependencies().get("casslog");
        if (casslog != null) {
            build.dependencies().add("casslog", MavenDependency.from(casslog));
        }
    }

}
