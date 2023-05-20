/*
 * Copyright 2012-2020 the original author or authors.
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

package io.spring.start.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectFormat;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.versionresolver.DependencyManagementVersionResolver;
import io.spring.initializr.web.controller.DefaultProjectGenerationController;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.*;
import io.spring.start.site.project.ProjectDescriptionCustomizerConfiguration;
import io.spring.start.site.security.ResourceSecurityConfiguration;
import io.spring.start.site.support.CacheableDependencyManagementVersionResolver;
import io.spring.start.site.support.StartInitializrMetadataUpdateStrategy;
import io.spring.start.site.web.HomeController;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Initializr website application.
 *
 * @author Stephane Nicoll
 */
@EnableAutoConfiguration
@SpringBootConfiguration
@Import({ProjectDescriptionCustomizerConfiguration.class, ResourceSecurityConfiguration.class})
@EnableCaching
@EnableAsync
public class StartApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }

    @Bean
    public HomeController homeController() {
        return new HomeController();
    }

    @Bean
    public StartInitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy(
            RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        return new StartInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), objectMapper);
    }

    @Bean
    public DependencyManagementVersionResolver dependencyManagementVersionResolver() throws IOException {
        return new CacheableDependencyManagementVersionResolver(DependencyManagementVersionResolver
                .withCacheLocation(Files.createTempDirectory("version-resolver-cache-")));
    }

    @Bean
    ProjectGenerationController<ProjectRequest> projectGenerationController(
            InitializrMetadataProvider metadataProvider,
            ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker) {
        return new DefaultProjectGenerationController(metadataProvider, projectGenerationInvoker);
    }

    @Bean
    public ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker(ApplicationContext applicationContext
            , DefaultProjectRequestToDescriptionConverter projectRequestToDescriptionConverter) {
        return new ProjectGenerationInvoker<>(applicationContext, projectRequestToDescriptionConverter);
    }

    @Bean
    public DefaultProjectRequestToDescriptionConverter projectRequestToDescriptionConverter(
            ObjectProvider<ProjectRequestPlatformVersionTransformer> platformVersionTransformer) {
        ProjectRequestPlatformVersionTransformer transformer = platformVersionTransformer.
                getIfAvailable(DefaultProjectRequestPlatformVersionTransformer::new);
        return new DefaultProjectRequestToDescriptionConverter(transformer) {
            @Override
            public ProjectDescription convert(ProjectRequest request, InitializrMetadata metadata) {
                MutableProjectDescription description = new MutableProjectDescription();
                convert(request, description, metadata);
                description.setProjectFormat(getProjectFormat(request, metadata));
                return description;
            }
        };
    }

    private ProjectFormat getProjectFormat(ProjectRequest request, InitializrMetadata metadata) {
        Type typeFromMetadata = metadata.getTypes().get(request.getType());
        return ProjectFormat.format(typeFromMetadata.getTags().get("format"));
    }

}
