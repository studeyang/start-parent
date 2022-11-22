package io.spring.start.site.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/20
 */
@Data
@Accessors(chain = true)
public class Metadata {
    private String requestId;
    private JenkinsCallback.Stage stage;
    private String jobName;
    private String namespacePath;
    private String serviceName;
    private String envName;

    public void nextStage() {
        this.stage = stage.next();
    }

    @JsonIgnore
    public boolean isValid() {
        if (requestId == null || requestId.isEmpty() || getStage() == null) {
            return false;
        }
        return requestId.startsWith("start.yourcompany.com@");
    }

}
