package io.spring.start.site.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.initializr.web.project.ProjectGenerationResult;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
@Data
public class CreateProjectResponse {

    private Integer id;

    private String name;

    @JsonProperty("ssh_url_to_repo")
    private String sshUrl;

    @JsonProperty("http_url_to_repo")
    private String httpUrl;

    @JsonProperty("web_url")
    private String webUrl;

    @JsonIgnore
    private ProjectGenerationResult projectGenerationResult;

}
