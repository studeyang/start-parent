package io.spring.start.site.security.biz;

import io.spring.start.site.security.bo.ProcessRequest;

/**
 * @author <a href="https://github.com/studeyang">studeyang</a>
 * @since 1.0 2022/9/14
 */
public interface TransactionProcessor<R> {

    /**
     * 执行逻辑处理
     *
     * @param request 请求
     * @return 结果
     * @throws ProcessException 处理异常
     */
    R process(ProcessRequest request) throws ProcessException;

    /**
     * 业务回滚
     *
     * @param result process的处理结果
     */
    void rollback(R result);

    /**
     * process处理成功回调
     *
     * @param result process的处理结果
     */
    default void onSuccess(R result) {
        // do nothing
    }

    /**
     * 业务流结束回调
     *
     * @param result process的处理结果
     */
    default void onFinish(R result) {
        // do nothing
    }

}
