package io.spring.initializr.zebra.contributor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

class ApplicationYmlProjectContributorTest {

    @Test
    void test_yaml() {
        Map<String, String> map = new HashMap<>();
        map.put("server.port", "11118");
        map.put("spring.application.name", "test-service");
        map.put("spring.datasource.user", "test");
        map.put("spring.datasource.password", "123456");

        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        Yaml yaml = new Yaml(options);
        System.out.println(yaml.dump(map));
    }

    @Test
    void mapToYaml() throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        map.put("server.port", 11118);
        map.put("spring.application.name", "test-service");
        map.put("spring.datasource.user", "test");
        map.put("spring.datasource.password", "123456");

        YAMLMapper yamlMapper = new YAMLMapper();
        yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        yamlMapper.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
        yamlMapper.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);

        String yaml = yamlMapper.writeValueAsString(map);
        System.out.println(yaml);
    }

}