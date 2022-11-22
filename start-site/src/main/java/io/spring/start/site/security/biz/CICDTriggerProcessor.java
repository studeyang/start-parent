package io.spring.start.site.security.biz;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.Error;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import io.spring.start.site.security.bo.ProcessRequest;
import io.spring.start.site.security.bo.VoidResult;
import io.spring.start.site.security.dto.JenkinsCallback;
import io.spring.start.site.security.dto.Metadata;
import io.spring.start.site.security.dto.RunProjectProcessRequest;
import io.spring.start.site.support.ObjectId;
import io.spring.start.site.support.utils.JsonUtils;
import io.spring.start.site.support.utils.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/10/14
 */
@Slf4j
public class CICDTriggerProcessor implements TransactionProcessor<VoidResult> {

    private final JenkinsClient client;

    public CICDTriggerProcessor() {
        client = newJenkinsClient();
    }

    @Override
    public VoidResult process(ProcessRequest request) throws ProcessException {
        RunProjectProcessRequest runProjectProcessRequest = (RunProjectProcessRequest) request;
        String branch = runProjectProcessRequest.getBranch();
        String namespacePath = runProjectProcessRequest.getGroupPath() + "/" + runProjectProcessRequest.getProjectName();
        String serviceName = runProjectProcessRequest.getWebProjectRequest().getArtifactId() + "-service";

        // 触发空值来刷新 Jenkins NAMESPACE_PATH 列表
        String requestId = ObjectId.getId();
        Metadata metadata = new Metadata()
                .setRequestId("start.yourcompany.com@" + requestId)
                .setStage(JenkinsCallback.Stage.PRECI)
                .setJobName(branch)
                .setNamespacePath(namespacePath)
                .setServiceName(serviceName);
        log.info("(1). 执行 PRECI");
        boolean success = triggerCI(metadata, "");
        if (!success) {
            throw new ProcessException("CICD 触发失败.");
        }
        return null;
    }

    public boolean triggerCI(Metadata metadata, String namespacePath) {
        Map<String, List<String>> properties = new HashMap<>();
        String jsonMetaData = JsonUtils.serializer(metadata);
        properties.put("METADATA", Lists.newArrayList(jsonMetaData));
        properties.put("NAMESPACE_PATH", Lists.newArrayList(namespacePath));
        properties.put("GIT_REF", Lists.newArrayList("refs/heads/master"));

        log.info("trigger ci METADATA: {}", jsonMetaData);
        IntegerResponse response = client.api().jobsApi()
                .buildWithParameters("CI-2.0", metadata.getJobName(), properties);
        boolean success = true;
        for (Error error : response.errors()) {
            log.error(error.message());
            success = false;
        }
        return success;
    }

    public void triggerCD(Metadata metadata, String serviceName) {
        Map<String, List<String>> properties = new HashMap<>();
        String jsonMetaData = JsonUtils.serializer(metadata);
        properties.put("METADATA", Lists.newArrayList(jsonMetaData));
        properties.put("ENV_NAME", Lists.newArrayList(metadata.getEnvName()));
        properties.put("SERVICE_NAME", Lists.newArrayList(serviceName));
        properties.put("DOCKER_IMAGE_VERSION", Lists.newArrayList("latest"));

        log.info("trigger cd METADATA: {}, SERVICE_NAME: {}", jsonMetaData, serviceName);
        IntegerResponse response = client.api().jobsApi()
                .buildWithParameters("CD", metadata.getJobName(), properties);
        for (Error error : response.errors()) {
            log.error(error.message());
        }
    }

    private JenkinsClient newJenkinsClient() {
        return JenkinsClient.builder()
                .endPoint("http://ops.dev.yourcompany.com/")
                .credentials("kmw:123456")
                .build();
    }

    @Override
    public void rollback(VoidResult result) {
        // no need
    }

}
