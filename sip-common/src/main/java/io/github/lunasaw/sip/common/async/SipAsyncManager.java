package io.github.lunasaw.sip.common.async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.exception.SipExceptionHandler;
import io.github.lunasaw.sip.common.exception.SipProcessorException;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP异步管理器
 * 协调异步处理和同步处理的执行策略
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
@Component
public class SipAsyncManager {

    private final SipAsyncExecutor    asyncExecutor;
    private final SipAsyncConfig      asyncConfig;
    private final SipExceptionHandler exceptionHandler;

    @Autowired
    public SipAsyncManager(SipAsyncExecutor asyncExecutor,
        SipAsyncConfig asyncConfig,
        SipExceptionHandler exceptionHandler) {
        this.asyncExecutor = asyncExecutor;
        this.asyncConfig = asyncConfig;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * 处理SIP请求，根据配置和处理器特性选择同步或异步执行
     *
     * @param processors 处理器列表
     * @param requestEvent 请求事件
     * @return 处理结果的Future列表
     */
    public CompletableFuture<Void> processRequest(List<SipRequestProcessor> processors, RequestEvent requestEvent) {
        if (!asyncConfig.isEnabled()) {
            // 异步未启用，执行同步处理
            return executeSyncRequest(processors, requestEvent);
        }

        // 检查是否所有处理器都支持异步
        boolean allAsyncSupported = processors.stream()
            .allMatch(processor -> processor instanceof SipAsyncProcessor &&
                ((SipAsyncProcessor)processor).isAsyncSupported());

        if (allAsyncSupported) {
            return executeAsyncRequest(processors, requestEvent);
        } else {
            // 混合模式：有些支持异步，有些不支持
            return executeMixedRequest(processors, requestEvent);
        }
    }

    /**
     * 处理SIP响应
     *
     * @param processor 响应处理器
     * @param responseEvent 响应事件
     * @return 处理结果
     */
    public CompletableFuture<Void> processResponse(SipResponseProcessor processor, ResponseEvent responseEvent) {
        if (!asyncConfig.isEnabled()) {
            return CompletableFuture.runAsync(() -> processor.process(responseEvent));
        }

        if (processor instanceof SipAsyncProcessor) {
            SipAsyncProcessor asyncProcessor = (SipAsyncProcessor)processor;
            if (asyncProcessor.isAsyncSupported()) {
                return asyncExecutor.executeAsync(asyncProcessor, responseEvent);
            }
        }

        // 不支持异步，执行同步处理
        return CompletableFuture.runAsync(() -> processor.process(responseEvent));
    }

    /**
     * 同步执行请求处理
     */
    private CompletableFuture<Void> executeSyncRequest(List<SipRequestProcessor> processors, RequestEvent requestEvent) {
        return CompletableFuture.runAsync(() -> {
            for (SipRequestProcessor processor : processors) {
                try {
                    processor.process(requestEvent);
                    log.debug("同步处理SIP请求成功: {}", processor.getClass().getSimpleName());
                } catch (Exception e) {
                    String processorName = processor.getClass().getSimpleName();
                    SipProcessorException processorException = SipExceptionHandler.createProcessorException(
                        processorName, null, "同步处理器执行异常", e);
                    log.error("同步处理器执行异常: {}", processorException.toString());
                    exceptionHandler.handleException(processorException, requestEvent);
                    break; // 遇到异常时停止后续处理器执行
                }
            }
        });
    }

    /**
     * 异步执行请求处理
     */
    private CompletableFuture<Void> executeAsyncRequest(List<SipRequestProcessor> processors, RequestEvent requestEvent) {
        // 创建异步任务列表
        CompletableFuture<Void>[] futures = processors.stream()
            .map(processor -> {
                SipAsyncProcessor asyncProcessor = (SipAsyncProcessor)processor;
                return asyncExecutor.executeAsync(asyncProcessor, requestEvent)
                    .exceptionally(throwable -> {
                        String processorName = processor.getClass().getSimpleName();
                        SipProcessorException processorException = SipExceptionHandler.createProcessorException(
                            processorName, null, "异步处理器执行异常", throwable);
                        log.error("异步处理器执行异常: {}", processorException.toString());
                        exceptionHandler.handleException(processorException, requestEvent);
                        return null;
                    });
            })
            .toArray(CompletableFuture[]::new);

        // 等待所有异步任务完成
        return CompletableFuture.allOf(futures);
    }

    /**
     * 混合模式执行请求处理（部分异步，部分同步）
     */
    private CompletableFuture<Void> executeMixedRequest(List<SipRequestProcessor> processors, RequestEvent requestEvent) {
        return CompletableFuture.runAsync(() -> {
            for (SipRequestProcessor processor : processors) {
                try {
                    if (processor instanceof SipAsyncProcessor) {
                        SipAsyncProcessor asyncProcessor = (SipAsyncProcessor)processor;
                        if (asyncProcessor.isAsyncSupported()) {
                            // 异步处理
                            asyncExecutor.executeAsync(asyncProcessor, requestEvent)
                                .exceptionally(throwable -> {
                                    log.error("混合模式异步处理异常: {}", processor.getClass().getSimpleName(), throwable);
                                    return null;
                                });
                            continue;
                        }
                    }

                    // 同步处理
                    processor.process(requestEvent);
                    log.debug("混合模式同步处理成功: {}", processor.getClass().getSimpleName());

                } catch (Exception e) {
                    String processorName = processor.getClass().getSimpleName();
                    SipProcessorException processorException = SipExceptionHandler.createProcessorException(
                        processorName, null, "混合模式处理器执行异常", e);
                    log.error("混合模式处理器执行异常: {}", processorException.toString());
                    exceptionHandler.handleException(processorException, requestEvent);
                    break; // 遇到异常时停止后续处理器执行
                }
            }
        });
    }

    /**
     * 获取异步执行器状态
     */
    public String getAsyncExecutorStatus() {
        return asyncExecutor.getPoolStatus();
    }

    /**
     * 检查异步处理是否启用
     */
    public boolean isAsyncEnabled() {
        return asyncConfig.isEnabled();
    }
}