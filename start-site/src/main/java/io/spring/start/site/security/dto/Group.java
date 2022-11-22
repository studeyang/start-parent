package io.spring.start.site.security.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/13
 */
@Data
public class Group {

    private Integer id;

    @JsonProperty("web_url")
    private String webUrl;

    private String name;

    private String key;

    private String path;

    private String description;

    public void setPath(String path) {
        this.key = path;
        this.path = path;
    }

}
