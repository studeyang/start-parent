package io.spring.start.site.security.resource;

import io.spring.start.site.security.SecurityProperties;
import io.spring.start.site.security.biz.CreateGitlabProjectProcessor;
import io.spring.start.site.security.biz.ProcessorChain;
import io.spring.start.site.security.dto.Branch;
import io.spring.start.site.security.dto.Group;
import io.spring.start.site.security.dto.Response;
import io.spring.start.site.security.dto.RunProjectProcessRequest;
import io.spring.start.site.support.AuthorizationSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/5
 */
@Slf4j
@RestController
@RequestMapping("gitlab")
public class GitlabResourceController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CreateGitlabProjectProcessor createGitlabProjectProcessor;
    @Autowired
    private SecurityProperties securityProperties;

    @GetMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<Group>> getGroups() {
        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithToken();

        ResponseEntity<Group[]> groups = restTemplate.exchange(
                securityProperties.getBaseUrl() + "/api/v4/groups",
                HttpMethod.GET,
                httpEntity,
                Group[].class);
        Response<List<Group>> response = groups.getBody() == null ?
                Response.data(new ArrayList<>()) :
                Response.data(Arrays.stream(groups.getBody()).collect(Collectors.toList()));
        log.info("response: {}", response);
        return response;
    }

    @GetMapping("/{id}/branches")
    public Response<List<Branch>> getBranches(@PathVariable("id") String id) {
//        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithAdminToken();
//        ResponseEntity<Branch[]> branches = restTemplate.exchange(
//                "https://gitlab.com/api/v4/projects/" + id + "/repository/branches",
//                HttpMethod.GET,
//                httpEntity,
//                Branch[].class);
//        Response<List<Branch>> response = branches.getBody() == null ?
//                Response.data(new ArrayList<>()) :
//                Response.data(Arrays.stream(branches.getBody()).collect(Collectors.toList()));
//        log.info("response: {}", response);
//        return response;
        return Response.data(new ArrayList<>());
    }

    @PostMapping("/project")
    public Response<Object> createProject(@RequestBody RunProjectProcessRequest request) {
        log.info("create project request: {}", request);
        return ProcessorChain.beginWith(createGitlabProjectProcessor)
//                .then(createDevopsProcessor)
//                .then(cicdTriggerProcessor)
                .startUp(request);
    }

}
