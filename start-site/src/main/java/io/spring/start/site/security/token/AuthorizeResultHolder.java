package io.spring.start.site.security.token;

import io.spring.start.site.security.UserInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/7
 */
public class AuthorizeResultHolder {

    @Setter
    @Getter
    private Token token;

    @Setter
    @Getter
    private UserInfo userInfo;

    public void refreshToken() {

    }

}
