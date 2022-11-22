package io.spring.initializr.generator.project.based;

import io.spring.initializr.generator.project.ProjectFormat;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/5
 */
public class BasedProjectFormat implements ProjectFormat {

    public static final String ID = "based-project";

    @Override
    public String format() {
        return ID;
    }

}
