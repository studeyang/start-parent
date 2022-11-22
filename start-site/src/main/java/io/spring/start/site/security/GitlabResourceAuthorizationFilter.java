package io.spring.start.site.security;

import io.spring.start.site.security.dto.Response;
import io.spring.start.site.security.token.AuthorizeResultHolder;
import io.spring.start.site.security.token.Token;
import io.spring.start.site.support.AuthorizationSupport;
import io.spring.start.site.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

import static io.spring.start.site.support.AuthorizationSupport.AUTHORIZATION;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/7
 */
@Slf4j
@RequiredArgsConstructor
public class GitlabResourceAuthorizationFilter extends GenericFilter {

    private final SecurityProperties properties;
    private final RestTemplate restTemplate;
    private final ApplicationContext applicationContext;

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        if (isValidToken((HttpServletRequest) request) || isAuthorized()) {
            chain.doFilter(request, response);
        } else {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getAuthorizationUri())
                    .queryParam("client_id", properties.getClientId())
                    .queryParam("redirect_uri", properties.getRedirectUri())
                    .queryParam("response_type", "code")
                    .queryParam("state", ((HttpServletRequest) request).getQueryString());
            String url = builder.toUriString();
            log.info("browser redirect to: {}", url);
            redirect(response.getWriter(), url);
        }

    }

    private void redirect(PrintWriter writer, String url) {
        Response<String> response = Response.redirect(url);
        writer.write(JsonUtils.serializer(response));
    }

    private boolean isAuthorized() {
        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithToken();

        try {
            ResponseEntity<UserInfo> userInfo = restTemplate.exchange(
                    properties.getUserInfoUri(),
                    HttpMethod.GET,
                    httpEntity,
                    UserInfo.class);
            saveUserInfo(userInfo.getBody());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidToken(HttpServletRequest request) {

        String accessToken = request.getHeader(AUTHORIZATION);
        if (accessToken == null || accessToken.isEmpty()) {
            return false;
        }

        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithToken(accessToken);
        try {
            ResponseEntity<UserInfo> userInfo = restTemplate.exchange(
                    properties.getUserInfoUri(),
                    HttpMethod.GET,
                    httpEntity,
                    UserInfo.class);
            saveUserInfo(userInfo.getBody());
            saveUserToken(accessToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveUserInfo(UserInfo userInfo) {
        applicationContext.getBean(AuthorizeResultHolder.class).setUserInfo(userInfo);
    }

    private void saveUserToken(String accessToken) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        applicationContext.getBean(AuthorizeResultHolder.class).setToken(token);
    }

}
