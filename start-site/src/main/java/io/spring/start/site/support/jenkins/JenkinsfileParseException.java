package io.spring.start.site.support.jenkins;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
public class JenkinsfileParseException extends RuntimeException {

    public JenkinsfileParseException(String message) {
        super(message);
    }

    public JenkinsfileParseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
