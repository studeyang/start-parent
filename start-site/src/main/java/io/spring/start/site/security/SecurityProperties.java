package io.spring.start.site.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    private String authorizationUri;

    private String tokenUri;

    private String userInfoUri;

    private String redirectUri;

    private String clientId;

    private String clientSecret;

}
