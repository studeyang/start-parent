package io.spring.initializr.generator.project;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 0.12.1 2022/7/5
 */
public interface ProjectFormatFactory {

    ProjectFormat create(String format);

}
