package io.spring.initializr.generator.project.zebra;

import io.spring.initializr.generator.project.ProjectFormat;
import io.spring.initializr.generator.project.ProjectFormatFactory;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/5
 */
public class ZebraProjectFormatFactory implements ProjectFormatFactory {

    @Override
    public ProjectFormat create(String format) {
        if (ZebraProjectFormat.ID.equals(format)) {
            return new ZebraProjectFormat();
        }
        return null;
    }

}
