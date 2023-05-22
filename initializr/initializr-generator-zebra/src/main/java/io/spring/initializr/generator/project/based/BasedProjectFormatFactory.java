package io.spring.initializr.generator.project.based;

import io.spring.initializr.generator.project.ProjectFormat;
import io.spring.initializr.generator.project.ProjectFormatFactory;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/5
 */
public class BasedProjectFormatFactory implements ProjectFormatFactory {

    @Override
    public ProjectFormat create(String format) {
        if (BasedProjectFormat.ID.equals(format)) {
            return new BasedProjectFormat();
        }
        return null;
    }

}
