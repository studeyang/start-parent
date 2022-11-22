package io.spring.start.site.security.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/11
 */
@Data
public class RepositoryFileBody {

    private String branch;

    @JsonProperty("author_email")
    private String authorEmail;

    @JsonProperty("author_name")
    private String authorName;

    private String content;

    @JsonProperty("commit_message")
    private String commitMessage;

}
