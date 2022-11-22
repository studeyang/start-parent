package io.spring.start.site.security;

import io.spring.start.site.security.biz.CICDTriggerProcessor;
import io.spring.start.site.security.dto.JenkinsCallback;
import io.spring.start.site.security.dto.Metadata;
import io.spring.start.site.support.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("jenkins")
public class JenkinsController {

    private final CICDTriggerProcessor cicdTriggerProcessor;

    @PostMapping("callback")
    public void callback(@RequestBody JenkinsCallback jenkinsCallback) {
        log.info("jenkins 回调: {}", JsonUtils.serializer(jenkinsCallback));
        Metadata metadata = jenkinsCallback.getMetadata();

        if (!metadata.isValid()) {
            return;
        }

        switch (jenkinsCallback.getMetadata().getStage()) {
            case PRECI:
                log.info("(2). 执行 CI");
                metadata.nextStage();
                cicdTriggerProcessor.triggerCI(metadata, metadata.getNamespacePath());
                break;
            case CI:
                log.info("(3). 执行 PRECD, ENV_NAME: {}", jenkinsCallback.getEnvName());
                metadata.nextStage();
                if (jenkinsCallback.getEnvName() == null) {
                    log.error("jenkins 回调返回 evnName 为 null.");
                } else {
                    metadata.setEnvName(jenkinsCallback.getEnvName());
                    cicdTriggerProcessor.triggerCD(metadata, "");
                }
                break;
            case PRECD:
                log.info("(4). 执行 CD");
                metadata.nextStage();
                cicdTriggerProcessor.triggerCD(metadata, metadata.getServiceName());
                break;
            case CD:
                // 是否要通知？
                if (jenkinsCallback.isSuccess()) {
                    log.info("CD 执行完成, requestId: {}", jenkinsCallback.getMetadata().getRequestId());
                } else {
                    log.error("CD 执行失败, requestId: {}", jenkinsCallback.getMetadata().getRequestId());
                }
                break;
        }
    }

}
