package io.spring.start.site.security.biz;

import io.spring.start.site.security.SecurityProperties;
import io.spring.start.site.security.bo.CreateDevopsResult;
import io.spring.start.site.security.bo.ProcessRequest;
import io.spring.start.site.security.dto.RepositoryFileBody;
import io.spring.start.site.security.dto.RunProjectProcessRequest;
import io.spring.start.site.support.AuthorizationSupport;
import io.spring.start.site.support.jenkins.JenkinsfileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/20
 */
@Slf4j
@RequiredArgsConstructor
public class CreateDevopsProcessor implements TransactionProcessor<CreateDevopsResult> {

    private static final String BASE_PATH = "/api/v4/projects/1944/repository/files";
    private static final String AUTHOR = "start.yourcompany.com";
    private static final String AUTHOR_EMAIL = "start@yourcompany.com";
    private static final String DEPLOY_CONTENT = "deployment:\n" +
            "  default:\n" +
            "    DOCKER_IMAGE_PATH: 'cassmall/{SERVICE_NAME}'\n" +
            "    RUN_CMD: \"['java', '-Xms512m', '-Xmx512m', '-Xss256k', '-XX:MaxMetaspaceSize=256m',\n" +
            "      '-XX:ReservedCodeCacheSize=250m', '-XX:+UseG1GC', '-Dlog.level=info', '-Dlog.level.cass=info',\n" +
            "      '-XX:OnOutOfMemoryError=/opt/agents/sre-sh/file_rename.sh /opt/agents/java-dump/{BRANCH}/{SERVICE_NAME}.hprof',\n" +
            "      '-XX:+HeapDumpOnOutOfMemoryError', '-XX:HeapDumpPath=/opt/agents/java-dump/{BRANCH}/{SERVICE_NAME}.hprof',\n" +
            "      '-Dsun.zip.disableMemoryMapping=true', '-jar', '/opt/{SERVICE_NAME}.jar']\"\n" +
            "    CPU_REQUEST: '250m'\n" +
            "    MEM_REQUEST: '1Gi'\n" +
            "    SERVICE_PORT: 11118\n" +
            "    HEALTH_PORT: 30000\n" +
            "    HEALTH_PATH: '/cass-metrics/info'\n" +
            "    READINESS_PROBE_DELAY: 35\n" +
            "    LIVENESS_PROBE_DELAY: 30\n" +
            "service:\n" +
            "  default:\n" +
            "    SERVICE_PORT: 11118\n" +
            "    CASSMALL_SERVICE_ID: '{SERVICE_NAME}'";
    private final RestTemplate restTemplate;

    private final SecurityProperties securityProperties;

    @Override
    public CreateDevopsResult process(ProcessRequest request) throws ProcessException {

        RunProjectProcessRequest runProjectProcessRequest = (RunProjectProcessRequest) request;

        updateJenkinsfile(runProjectProcessRequest);
        try {
            createDeployFile(runProjectProcessRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ProcessException("上传 deploy.yml 文件失败.");
        }

        return new CreateDevopsResult();
    }

    void createDeployFile(RunProjectProcessRequest runProjectProcessRequest) {
        String url = securityProperties.getBaseUrl() + BASE_PATH + "/{file}";

        RepositoryFileBody body = new RepositoryFileBody();
        body.setBranch(runProjectProcessRequest.getBranch());
        body.setAuthorName(AUTHOR);
        body.setAuthorEmail(AUTHOR_EMAIL);
        body.setContent(getDeployContent(runProjectProcessRequest));
        body.setCommitMessage("一键运行 " + runProjectProcessRequest.getProjectName());

        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithAdminToken(body);
        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class,
                    "/deploy/configs/" + runProjectProcessRequest.getProjectName() + "-service.yml");
        } catch (Exception e) {
            if (!e.getMessage().contains("A file with this name already exists")) {
                throw new ProcessException(e.getMessage());
            }
        }
    }

    String getDeployContent(RunProjectProcessRequest runProjectProcessRequest) {
        String content = DEPLOY_CONTENT.replace("{BRANCH}", runProjectProcessRequest.getBranch());
        content = content.replace("{SERVICE_NAME}", runProjectProcessRequest.getProjectName() + "-service");
        return content;
    }

    void updateJenkinsfile(RunProjectProcessRequest runProjectProcessRequest) {
        String url = securityProperties.getBaseUrl() + BASE_PATH + "/{file}";

        RepositoryFileBody body = new RepositoryFileBody();
        body.setBranch(runProjectProcessRequest.getBranch());
        body.setAuthorName(AUTHOR);
        body.setAuthorEmail(AUTHOR_EMAIL);
        body.setContent(getJenkinsfile(runProjectProcessRequest));
        body.setCommitMessage("一键运行 " + runProjectProcessRequest.getProjectName());

        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithAdminToken(body);
        restTemplate.exchange(
                url,
                HttpMethod.PUT,
                httpEntity,
                String.class,
                "/deploy/Jenkinsfile");
    }

    private String getJenkinsfile(RunProjectProcessRequest runProjectProcessRequest) {
        String url = securityProperties.getBaseUrl() + BASE_PATH + "/{file}/raw" + "?ref=" + runProjectProcessRequest.getBranch();

        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithAdminToken();
        ResponseEntity<String> file = restTemplate.exchange(
                url,
                HttpMethod.GET,
                httpEntity,
                String.class,
                "/deploy/Jenkinsfile");

        return new JenkinsfileReader(file.getBody())
                .addChoices(runProjectProcessRequest.getProjectName() + "-service")
                .getContent();
    }

    @Override
    public void rollback(CreateDevopsResult result) {
        // 删除 icec-devops 里的文件
    }

}
