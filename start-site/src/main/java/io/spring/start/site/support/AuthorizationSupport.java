package io.spring.start.site.support;

import io.spring.start.site.security.token.AuthorizeResultHolder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.Optional;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/13
 */
public class AuthorizationSupport implements ApplicationContextAware {

    public static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ADMIN_TOKEN = "Bearer yanL_SKRdypnm5E4ztzb";
    private static ApplicationContext applicationContext;

    private static void initApplicationContext(ApplicationContext applicationContext) {
        AuthorizationSupport.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        initApplicationContext(applicationContext);
    }

    public static void addAccessToken(HttpHeaders headers,
                                      AuthorizeResultHolder authorizeResultHolder) {
        Optional.ofNullable(authorizeResultHolder.getToken()).ifPresent(token ->
                headers.add(AUTHORIZATION, BEARER_PREFIX + token.getAccessToken())
        );
    }

    public static HttpEntity<Object> fillHeaderWithToken() {

        AuthorizeResultHolder authorizeResultHolder = applicationContext.getBean(AuthorizeResultHolder.class);
        HttpHeaders headers = new HttpHeaders();

        Optional.ofNullable(authorizeResultHolder.getToken()).ifPresent(token ->
                headers.add(AUTHORIZATION, BEARER_PREFIX + token.getAccessToken())
        );

        return new HttpEntity<>(null, headers);
    }

    public static HttpEntity<Object> fillHeaderWithAdminToken() {
        return fillHeaderWithToken(ADMIN_TOKEN, null);
    }

    public static HttpEntity<Object> fillHeaderWithAdminToken(Object body) {
        return fillHeaderWithToken(ADMIN_TOKEN, body);
    }

    public static HttpEntity<Object> fillHeaderWithToken(String token) {
        return fillHeaderWithToken(token, null);
    }

    public static HttpEntity<Object> fillHeaderWithToken(String token, Object body) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, token);

        return new HttpEntity<>(body, headers);
    }

}
