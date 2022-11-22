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

    private String baseUrl;

    /**
     * 认证地址
     */
    private String authorizationUri;

    /**
     * 获取 token 地址
     */
    private String tokenUri;

    /**
     * 获取用户信息地址
     */
    private String userInfoUri;

    /**
     * 重定向地址（配置 start-site 后端接口）
     */
    private String redirectUri;

    private String clientId;

    private String clientSecret;

    /**
     * 管理员账号，供 git 上传工程文件使用
     */
    private Admin admin;

    @Data
    public static class Admin {
        private String name;
        private String password;
    }

}
