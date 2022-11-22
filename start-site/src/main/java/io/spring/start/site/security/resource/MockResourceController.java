package io.spring.start.site.security.resource;

import io.spring.start.site.security.dto.RunProjectProcessRequest;
import io.spring.start.site.security.dto.CreateProjectResponse;
import io.spring.start.site.security.dto.Response;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/5
 */
@RestController
@RequestMapping("mock")
public class MockResourceController {

    @GetMapping("/groups")
    public List<Map<String, Object>> getGroupsMock() {
        List<Map<String, Object>> list = new ArrayList<>();

        list.add(create("电商开源组件", "open"));
        list.add(create("技术组件组", "kmw"));

        return list;
    }

    private Map<String, Object> create(String name, String path) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", path);
        map.put("name", name);
        map.put("path", path);
        return map;
    }

    @PostMapping("/project")
    public Response<Object> createProjectMock(@RequestBody RunProjectProcessRequest request) {
        return Response.instance(true, new CreateProjectResponse());
    }

}
