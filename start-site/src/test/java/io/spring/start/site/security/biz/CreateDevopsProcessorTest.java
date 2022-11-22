package io.spring.start.site.security.biz;

import io.spring.start.site.security.SecurityProperties;
import io.spring.start.site.security.bo.ProcessRequest;
import io.spring.start.site.security.dto.RunProjectProcessRequest;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/11
 */
public class CreateDevopsProcessorTest {

    private CreateDevopsProcessor createDevopsProcessor = new CreateDevopsProcessor(
            new RestTemplate(), new SecurityProperties());

    @Test
    public void process() {
        createDevopsProcessor.process(new ProcessRequest());
    }

    @Test
    public void test_updateJenkinsfile() {
        RunProjectProcessRequest request = new RunProjectProcessRequest();
        request.setBranch("ci-trigger");
        request.setProjectName("test");

        createDevopsProcessor.updateJenkinsfile(request);
    }

    @Test
    public void test_getDeployContent() {
        RunProjectProcessRequest request = new RunProjectProcessRequest();
        request.setBranch("ci-trigger");
        request.setProjectName("test");

        String content = createDevopsProcessor.getDeployContent(request);
        System.out.println(content);
    }

    @Test
    public void test_replace() {
        String string = "{BRANCH}/hhh/{BRANCH}";
        System.out.println(string.replace("{BRANCH}", "==="));
    }

}