package io.spring.start.site.security.resource;

import io.spring.start.site.security.dto.Branch;
import io.spring.start.site.support.AuthorizationSupport;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/13
 */
public class GitlabResourceControllerTest {

    @Test
    public void test_getBranches() {
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithAdminToken();
        ResponseEntity<Branch[]> response = restTemplate.exchange(
                "https://gitlab.com/api/v4/projects/1944/repository/branches",
                HttpMethod.GET,
                httpEntity,
                Branch[].class);
        for (Branch branch : response.getBody()) {
            System.out.println(branch);
        }
    }

}