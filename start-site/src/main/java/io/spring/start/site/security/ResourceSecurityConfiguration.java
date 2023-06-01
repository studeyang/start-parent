package io.spring.start.site.security;

import io.spring.initializr.web.project.*;
import io.spring.start.site.security.biz.CreateDevopsProcessor;
import io.spring.start.site.security.biz.CreateGitlabProjectProcessor;
import io.spring.start.site.security.resource.GitlabResourceController;
import io.spring.start.site.security.resource.MockResourceController;
import io.spring.start.site.security.token.AuthorizeResultHolder;
import io.spring.start.site.support.AuthorizationSupport;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.annotation.SessionScope;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/5
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class ResourceSecurityConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<GitlabResourceAuthorizationFilter> gitlabResourceAuthorizationFilter(
            SecurityProperties properties, RestTemplate restTemplate, ApplicationContext applicationContext) {
        FilterRegistrationBean<GitlabResourceAuthorizationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new GitlabResourceAuthorizationFilter(properties, restTemplate, applicationContext));
        registration.addUrlPatterns("/gitlab/*");
        return registration;
    }

    @Bean
    public GitlabResourceController gitlabResourceController() {
        return new GitlabResourceController();
    }

    @Bean
    public MockResourceController mockResourceController() {
        return new MockResourceController();
    }

    @Bean
    public OAuth2Controller oAuth2Controller() {
        return new OAuth2Controller();
    }

    @Bean
    @SessionScope
    public AuthorizeResultHolder authorizeResultHolder() {
        return new AuthorizeResultHolder();
    }

    @Bean
    public AuthorizationSupport authorizationSupport() {
        return new AuthorizationSupport();
    }

    /* business */

    @Bean
    public CreateGitlabProjectProcessor createGitlabProjectProcessor(
            RestTemplate restTemplate,
            ApplicationContext applicationContext,
            ObjectProvider<ProjectRequestPlatformVersionTransformer> platformVersionTransformer,
            SecurityProperties securityProperties) {
        ProjectRequestPlatformVersionTransformer transformer =
                platformVersionTransformer.getIfAvailable(DefaultProjectRequestPlatformVersionTransformer::new);
        ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker = new ProjectGenerationInvoker<>(
                applicationContext, new DefaultProjectRequestToDescriptionConverter(transformer));
        return new CreateGitlabProjectProcessor(restTemplate, projectGenerationInvoker, securityProperties);
    }

    @Bean
    public CreateDevopsProcessor createDevopsProcessor(
            RestTemplate restTemplate, SecurityProperties securityProperties) {
        return new CreateDevopsProcessor(restTemplate, securityProperties);
    }

}