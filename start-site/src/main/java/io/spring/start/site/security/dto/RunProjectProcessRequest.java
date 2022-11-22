package io.spring.start.site.security.dto;

import io.spring.initializr.web.project.WebProjectRequest;
import io.spring.start.site.security.bo.ProcessRequest;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
@Data
public class RunProjectProcessRequest extends ProcessRequest {

    private String path;
    private String projectName;
    private String groupPath;
    private Integer namespaceId;
    private String branch;

    private WebProjectRequest webProjectRequest;

}
