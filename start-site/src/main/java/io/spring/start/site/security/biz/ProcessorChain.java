package io.spring.start.site.security.biz;

import io.spring.start.site.security.bo.ProcessRequest;
import io.spring.start.site.security.dto.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessorChain {

    private final List<TransactionProcessor> registry = new ArrayList<>();
    private final List<TransactionProcessor> processed = new ArrayList<>();

    private final Map<Class<? extends TransactionProcessor>, Object> processResults = new HashMap<>();

    public static ProcessorChain beginWith(TransactionProcessor processor) {
        ProcessorChain chain = new ProcessorChain();
        chain.registry.add(processor);
        return chain;
    }

    public ProcessorChain then(TransactionProcessor processor) {
        registry.add(processor);
        return this;
    }

    public Object getProcessResult(Class<? extends TransactionProcessor> processor) {
        return processResults.get(processor);
    }

    public Response<Object> startUp(ProcessRequest processRequest) {

        Exception exception = null;

        for (TransactionProcessor pr : registry) {
            try {
                Object result = pr.process(processRequest);
                pr.onSuccess(result);
                processResults.put(pr.getClass(), result);
                processed.add(pr);
            } catch (Exception e) {
                exception = e;
                log.error(e.getMessage(), e);
                logProgress();

                rollback(pr);
                // 反向遍历
                for (int i = processed.size() - 1; i >= 0; i--) {
                    rollback(processed.get(i));
                }

                break;
            }
        }

        if (exception == null) {
            processed.forEach(pd -> pd.onFinish(processResults.get(pd.getClass())));
            Object createProjectResponse = processResults.get(CreateGitlabProjectProcessor.class);
            return Response.instance(true, createProjectResponse);
        } else {
            return Response.instance(false, exception.getMessage());
        }
    }

    private void logProgress() {
        String errorProgress = processed.stream()
                .map(tp -> tp.getClass().getSimpleName()).collect(Collectors.joining(","));
        String progress = registry.stream()
                .map(tp -> tp.getClass().getSimpleName()).collect(Collectors.joining(","));
        StringBuilder progressBuilder = new StringBuilder();
        for (int i = 0; i < errorProgress.length(); i++) {
            progressBuilder.append("=");
        }
        for (int i = 0; i < progress.replace(errorProgress, "").length() - ">error".length(); i++) {
            if (i == 0) {
                progressBuilder.append(">error");
            }
            progressBuilder.append("-");
        }
        log.error("\n{}\n{}", progressBuilder, progress);
    }

    private void rollback(TransactionProcessor processor) {
        try {
            processor.rollback(getProcessResult(processor.getClass()));
        } catch (Exception e) {
            // ignore rollback exception
            log.error(e.getMessage(), e);
        }
    }

}
