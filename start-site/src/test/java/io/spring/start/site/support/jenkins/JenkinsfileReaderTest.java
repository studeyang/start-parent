package io.spring.start.site.support.jenkins;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Stack;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/10
 */
public class JenkinsfileReaderTest {

    private Path jenkinsfilePath;

    @Before
    public void setUp() throws URISyntaxException {
        URI uri = JenkinsfileReaderTest.class.getResource("/Jenkinsfile").toURI();
        jenkinsfilePath = Paths.get(uri);
    }

    @Test
    public void parse() throws IOException {
        JenkinsfileReader jenkinsfileReader = new JenkinsfileReader(jenkinsfilePath);
        jenkinsfileReader.addChoices("haha").getContent();
    }

    @Test
    public void test_stack() {
        Stack<String> brackets = new Stack<>();
        brackets.add("(");
        brackets.add(")");
        brackets.pop();

        System.out.println(brackets);
    }

}