package io.spring.start.site.security.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/7
 */
@Data
public class Token {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("access_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Long expiresIn;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("create_at")
    private Long createAt;

}
