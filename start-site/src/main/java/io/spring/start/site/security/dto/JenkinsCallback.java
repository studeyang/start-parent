package io.spring.start.site.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/20
 */
@Data
public class JenkinsCallback {

    private Metadata metadata;
    private boolean success;
    private String envName;

    @AllArgsConstructor
    public enum Stage {
        PRECI(1), CI(2), PRECD(3), CD(4);
        private int code;

        public Stage next() {
            if (this.code == CD.code) {
                throw new IllegalStateException("已是最后阶段，无下一阶段");
            }
            return codeOf(this.code + 1);
        }

        private Stage codeOf(int code) {
            for (Stage stage : Stage.values()) {
                if (stage.code == code) {
                    return stage;
                }
            }
            return null;
        }
    }

}
