package io.spring.start.site;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/21
 */
public class JenkinsRestTest {

    JenkinsClient client = JenkinsClient.builder()
            .endPoint("http://ops.dev.yourcompany.com/")
            .credentials("kmw:123456")
            .build();

    @Test
    public void testCI() {
        Map<String, List<String>> properties = new HashMap<>();
        properties.put("NAMESPACE_PATH", Lists.newArrayList(""));
        properties.put("GIT_REF", Lists.newArrayList("refs/heads/master"));
        properties.put("METADATA", Lists.newArrayList("{\"requestId\":\"start.yourcompany.com@6357413e4a5eac0edc316da1\",\"stage\":\"PRECI\",\"jobName\":\"infra\",\"namespacePath\":\"open/icec-cloud-test8\",\"serviceName\":\"icec-cloud-test8-service\"}"));

        IntegerResponse response = client.api().jobsApi()
                .buildWithParameters("CI-2.0", "infra", properties);
        for (Error error : response.errors()) {
            System.out.println("===>" + error.message());
        }
    }

    @Test
    public void testCD() {
        Map<String, List<String>> properties = new HashMap<>();
        properties.put("ENV_NAME", Lists.newArrayList("adv-center"));
        properties.put("SERVICE_NAME", Lists.newArrayList("adv-media-service"));
        properties.put("DOCKER_IMAGE_VERSION", Lists.newArrayList("latest"));

        IntegerResponse response = client.api().jobsApi()
                .buildWithParameters("CD", "adv-center", properties);
        System.out.println(response);
    }

    @Test
    public void testUpdateConfigXml() {

        String configXml = "";

        System.out.println(
                client.api().jobsApi()
                        .config("CI-2.0", "infra", configXml)
        );
    }

    @Test
    public void testDescription() {
        System.out.println(
                client.api().jobsApi()
                .description(null, "CI-2.0")
        );
    }

    @Test
    public void testJob() {
        System.out.println(
                client.api().jobsApi()
                        .jobInfo("", "CI-2.0").description()
        );
    }

}
