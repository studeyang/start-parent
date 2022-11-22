package io.spring.start.site.security;

import io.spring.start.site.security.token.AuthorizeResultHolder;
import io.spring.start.site.security.token.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/7
 */
@Slf4j
@RestController
@RequestMapping("oauth")
public class OAuth2Controller {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private SecurityProperties properties;

    @GetMapping("redirect")
    public void code2token(@RequestParam("code") String code,
                           @RequestParam(value = "state", required = false) String state,
                           HttpServletResponse response) throws IOException {
        MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", properties.getClientId());
        paramMap.add("client_secret", properties.getClientSecret());
        paramMap.add("code", code);
        paramMap.add("grant_type", "authorization_code");
        paramMap.add("redirect_uri", properties.getRedirectUri());

        Token token = restTemplate.postForObject(properties.getTokenUri(), paramMap, Token.class);
        saveToken(token);
        log.info("token: {}", token);
        log.info("state: {}", state);

        response.sendRedirect("/#!" + state);
    }

    private void saveToken(Token token) {
        authorizeResultHolder().setToken(token);
    }

    private AuthorizeResultHolder authorizeResultHolder() {
        return applicationContext.getBean(AuthorizeResultHolder.class);
    }

}
