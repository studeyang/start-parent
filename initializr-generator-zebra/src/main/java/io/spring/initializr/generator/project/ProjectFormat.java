package io.spring.initializr.generator.project;

import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.Objects;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/5
 */
public interface ProjectFormat {

    String format();

    static ProjectFormat format(String format) {
        return SpringFactoriesLoader.loadFactories(ProjectFormatFactory.class, ProjectFormat.class.getClassLoader())
                .stream()
                .map(factory -> factory.create(format)).filter(Objects::nonNull).findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Unrecognized project format '" + format + "'"));
    }

}
