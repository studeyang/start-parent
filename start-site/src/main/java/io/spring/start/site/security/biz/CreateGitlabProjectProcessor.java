package io.spring.start.site.security.biz;

import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectGenerationResult;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.WebProjectRequest;
import io.spring.start.site.security.bo.ProcessRequest;
import io.spring.start.site.security.dto.CreateProjectResponse;
import io.spring.start.site.security.dto.RunProjectProcessRequest;
import io.spring.start.site.support.AuthorizationSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
@Slf4j
@RequiredArgsConstructor
public class CreateGitlabProjectProcessor implements TransactionProcessor<CreateProjectResponse> {

    private final RestTemplate restTemplate;
    private final ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker;

    @Override
    public CreateProjectResponse process(ProcessRequest processRequest) throws ProcessException {
        RunProjectProcessRequest runProjectProcessRequest = (RunProjectProcessRequest) processRequest;
        CreateProjectResponse result;

        // 创建 gitlab 工程
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://gitlab.yourcompany.net/api/v4/projects")
                    .queryParam("name", runProjectProcessRequest.getProjectName())
                    .queryParam("path", runProjectProcessRequest.getPath())
                    .queryParam("namespace_id", runProjectProcessRequest.getNamespaceId());
            HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithToken();
            ResponseEntity<CreateProjectResponse> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    httpEntity,
                    CreateProjectResponse.class);
            result = response.getBody();
        } catch (Exception e) {
            throw new ProcessException("创建 Gitlab 工程失败: " + e.getMessage(), e);
        }

        // 生成工程
        try {
            WebProjectRequest webProjectRequest = runProjectProcessRequest.getWebProjectRequest();
            log.info("generate project: {}", webProjectRequest.getArtifactId());
            ProjectGenerationResult generationResult = projectGenerationInvoker.invokeProjectStructureGeneration(webProjectRequest);
            assert result != null;
            result.setProjectGenerationResult(generationResult);
        } catch (Exception e) {
            throw new ProcessException("生成工程文件失败: " + e.getMessage(), e);
        }

        return result;
    }

    @Override
    public void rollback(CreateProjectResponse result) {
        log.info("rollback: {}", result);
        // 清理的生成工程
        clean(result);
        // 删除 gitlab 工程
        if (result == null || result.getId() == null) {
            return;
        }
        Integer projectId = result.getId();

        HttpEntity<Object> httpEntity = AuthorizationSupport.fillHeaderWithToken();
        try {
            restTemplate.exchange(
                    "https://gitlab.yourcompany.net/api/v4/projects/" + projectId,
                    HttpMethod.DELETE,
                    httpEntity,
                    Void.class);
        } catch (Exception e) {
            log.error("project {} rollback fail.", projectId, e);
        }
    }

    @Override
    public void onSuccess(CreateProjectResponse result) {
        // 添加 hooks

        // 上传工程代码至 gitlab
        StringBuilder command = new StringBuilder();

        String projectPath = result.getProjectGenerationResult().getRootDirectory().resolve(result.getName()).toString();
        if (System.getProperty("os.name").contains("Windows")) {
            command.append(projectPath, 0, 2).append(" && ");
        }

        command.append("cd ").append(projectPath).append(" && ");
        command.append("git init && ");
        command.append("git add . && ");
        String projectName = result.getProjectGenerationResult().getProjectDescription().getArtifactId();
        command.append("git commit -m 一键运行:").append(projectName).append(" && ");
        String gitUrl = result.getHttpUrl().replace("https://", "https://scan:scan%40casstime2021@");
        command.append("git push -f ").append(gitUrl).append(" master:master");

        exec(command.toString());
    }

    @Override
    public void onFinish(CreateProjectResponse result) {
        clean(result);
    }

    private void clean(CreateProjectResponse result) {
        if (result == null) {
            return;
        }
        this.projectGenerationInvoker.cleanTempFiles(result.getProjectGenerationResult().getRootDirectory());
    }

    private void exec(String command) {
        Process process;
        String[] commands;
        if (System.getProperty("os.name").contains("Windows")) {
            commands = new String[] {"cmd.exe", "/c", command};
        } else {
            commands = new String[] {"sh", "-c", command};
        }
        try {
            log.info("执行命令: {}", command);
            process = Runtime.getRuntime().exec(commands);

            String execResult = getExecResult(process.getInputStream());
            if (StringUtils.isNotEmpty(execResult)) {
                log.info("exec result:\n{}", execResult);
            }

            String errorResult = getExecResult(process.getErrorStream());
            if (StringUtils.isNotEmpty(errorResult)) {
                log.error("exec error:\n{}", errorResult);
            }
        } catch (IOException e) {
            throw new ProcessException("执行 cmd 命令失败: " + e.getMessage(), e);
        }
    }

    private String getExecResult(InputStream inputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

}
