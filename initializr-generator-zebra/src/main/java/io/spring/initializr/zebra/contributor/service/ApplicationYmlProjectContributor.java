/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.initializr.zebra.contributor.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.project.contributor.SingleResourceProjectContributor;
import lombok.NoArgsConstructor;
import org.springframework.util.FileCopyUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * A {@link SingleResourceProjectContributor} that contributes a
 * {@code bootstrap.yml} file to a project.
 *
 * @author Stephane Nicoll
 */
public class ApplicationYmlProjectContributor implements ProjectContributor {

    private final ProjectDescription description;
    private final YAMLMapper yamlMapper;

    public ApplicationYmlProjectContributor(ProjectDescription description) {
        this.description = description;
        this.yamlMapper = new YAMLMapper();
        this.yamlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.yamlMapper.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
        this.yamlMapper.configure(YAMLGenerator.Feature.WRITE_DOC_START_MARKER, false);
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Path output = projectRoot.resolve(description.getArtifactId()
                + "-service/src/main/resources/" + getFileName());
        if (!Files.exists(output)) {
            Files.createDirectories(output.getParent());
            Files.createFile(output);
        }

        Property property = new Property();
        // server
        addServer(property);
        // spring
        property.put("spring", getSpringProperty());
        // management
        addManagementProperty(property);
        // mybatis
        addMybatisProperty(property);
        // mybatis-plus
        addMybatisPlusProperty(property);
        // panda
        addPandaProperty(property);

        String content = yamlMapper.writeValueAsString(property);
        FileCopyUtils.copy(new ByteArrayInputStream(content.getBytes()),
                Files.newOutputStream(output, StandardOpenOption.APPEND));
    }

    private void addServer(Property property) {
        if (hasDependenciesAnd("web")) {
            property.put("server", new Property("port", 11118));
        }
    }

    private String getFileName() {
        if (hasDependenciesAnd("web")) {
            return "bootstrap.yml";
        }
        return "application.yml";
    }

    private void addPandaProperty(Property property) {
        if (hasDependenciesAnd("panda-starter")) {
            property.put("panda", panda ->
                    panda.put("config", config -> {
                        config.put("server-addr", "http://panda.yourcompany.com");
                        config.put("product", "cassmall");
                        config.put("instance", "cassmall-panda-example");
                    }));
        }
    }

    private void addMybatisPlusProperty(Property property) {
        if (hasDependenciesAnd("mybatis-plus")) {
            property.put("mybatis-plus", mybatisPlus -> {
                mybatisPlus.put("mapper-locations", "classpath:" + getPackagePath() + "/service/dao/xml/*.xml");
                mybatisPlus.put("configuration", configuration -> {
                    configuration.put("map-underscore-to-camel-case", true);
                    configuration.put("log-impl", "org.apache.ibatis.logging.stdout.StdOutImpl");
                });
                mybatisPlus.put("global-config", globalConfig ->
                        globalConfig.put("db-config", dbConfig -> {
                            dbConfig.put("logic-delete-value", 0);
                            dbConfig.put("logic-not-delete-value", 1);
                        }));
            });
        }
    }

    private void addMybatisProperty(Property property) {
        if (hasDependenciesAnd("mybatis")) {
            property.put("mybatis", mybatis ->
                    mybatis.put("mapper-locations", "classpath:" + getPackagePath() + "/service/dao/xml/*.xml")
            );
        }
    }

    private String getPackagePath() {
        return Optional.of(description)
                .map(ProjectDescription::getPackageName)
                .map(packageName -> packageName.replace(".", "/"))
                .orElse("");
    }

    private void addManagementProperty(Property property) {
        if (hasDependenciesAnd("actuator")) {
            property.put("management", management -> {
                management.put("port", 21118);
                management.put("contextPath", "/hellgate");
                management.put("security.enabled", false);
            });
        }
    }

    private boolean hasDependenciesAnd(String... dependencies) {
        for (String dependency : dependencies) {
            if (!description.getRequestedDependencies().containsKey(dependency)) {
                return false;
            }
        }
        return true;
    }

    private Property getSpringProperty() {
        Property spring = new Property();
        spring.put("application", new Property("name", description.getArtifactId() + "-service"));
        if (hasDependenciesAnd("cloud-config-client")) {
            spring.put("cloud", cloud ->
                    cloud.put("config", config -> {
                        config.put("uri", "http://config-center.alpha-intra.yourcompany.com/conf");
                        config.put("label", "alpha");
                    }));
        }
        return spring;
    }

    /**
     * 把它当成 Map 就好
     */
    @NoArgsConstructor
    static class Property extends LinkedHashMap<String, Object> {

        Property(String key, String value) {
            super.put(key, value);
        }

        Property(String key, Integer value) {
            super.put(key, value);
        }

        public void put(String key, Consumer<Property> consumer) {
            Property property = new Property();
            consumer.accept(property);
            super.put(key, property);
        }
    }

}
