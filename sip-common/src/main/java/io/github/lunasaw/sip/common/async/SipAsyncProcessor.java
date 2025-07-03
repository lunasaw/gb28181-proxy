package io.github.lunasaw.sip.common.async;

import java.util.concurrent.CompletableFuture;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

/**
 * SIP异步处理器接口
 * 定义异步处理SIP消息的能力，提升系统并发性能
 *
 * @author luna
 * @date 2024/01/01
 */
public interface SipAsyncProcessor {

    /**
     * 异步处理SIP请求
     *
     * @param requestEvent 请求事件
     * @return 异步处理结果
     */
    default CompletableFuture<Void> processAsync(RequestEvent requestEvent) {
        return CompletableFuture.runAsync(() -> processSync(requestEvent));
    }

    /**
     * 异步处理SIP响应
     *
     * @param responseEvent 响应事件
     * @return 异步处理结果
     */
    default CompletableFuture<Void> processAsync(ResponseEvent responseEvent) {
        return CompletableFuture.runAsync(() -> processSync(responseEvent));
    }

    /**
     * 同步处理SIP请求（兼容原有接口）
     *
     * @param requestEvent 请求事件
     */
    default void processSync(RequestEvent requestEvent) {
        // 默认空实现，子类可选择实现
    }

    /**
     * 同步处理SIP响应（兼容原有接口）
     *
     * @param responseEvent 响应事件
     */
    default void processSync(ResponseEvent responseEvent) {
        // 默认空实现，子类可选择实现
    }

    /**
     * 判断是否支持异步处理
     *
     * @return true表示支持异步处理，false表示只支持同步处理
     */
    default boolean isAsyncSupported() {
        return true;
    }

    /**
     * 获取处理器名称
     *
     * @return 处理器名称
     */
    default String getProcessorName() {
        return this.getClass().getSimpleName();
    }
}