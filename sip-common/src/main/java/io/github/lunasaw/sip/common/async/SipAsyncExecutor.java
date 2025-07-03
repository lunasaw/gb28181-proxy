package io.github.lunasaw.sip.common.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.exception.SipException;
import io.github.lunasaw.sip.common.exception.SipErrorType;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP异步执行器
 * 管理异步任务的执行和线程池
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
@Component
public class SipAsyncExecutor {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final SipAsyncConfig         asyncConfig;

    @Autowired
    public SipAsyncExecutor(SipAsyncConfig asyncConfig) {
        this.asyncConfig = asyncConfig;
        this.taskExecutor = createTaskExecutor();
    }

    /**
     * 创建线程池任务执行器
     */
    private ThreadPoolTaskExecutor createTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncConfig.getCorePoolSize());
        executor.setMaxPoolSize(asyncConfig.getMaxPoolSize());
        executor.setQueueCapacity(asyncConfig.getQueueCapacity());
        executor.setKeepAliveSeconds(asyncConfig.getKeepAliveSeconds());
        executor.setThreadNamePrefix(asyncConfig.getThreadNamePrefix());
        executor.setWaitForTasksToCompleteOnShutdown(asyncConfig.isWaitForTasksToCompleteOnShutdown());
        executor.setAwaitTerminationSeconds(asyncConfig.getAwaitTerminationSeconds());

        // 配置拒绝策略
        if (asyncConfig.isCallerRunsPolicy()) {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        } else {
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        }

        executor.initialize();

        log.info("SIP异步执行器初始化完成 - 核心线程数: {}, 最大线程数: {}, 队列容量: {}",
            asyncConfig.getCorePoolSize(), asyncConfig.getMaxPoolSize(), asyncConfig.getQueueCapacity());

        return executor;
    }

    /**
     * 异步执行SIP请求处理
     *
     * @param processor 异步处理器
     * @param requestEvent 请求事件
     * @return 异步处理结果
     */
    public CompletableFuture<Void> executeAsync(SipAsyncProcessor processor, RequestEvent requestEvent) {
        if (!asyncConfig.isEnabled() || !processor.isAsyncSupported()) {
            // 如果异步未启用或处理器不支持异步，则同步执行
            return CompletableFuture.runAsync(() -> processor.processSync(requestEvent));
        }

        return CompletableFuture.runAsync(() -> {
            try {
                processor.processSync(requestEvent);
                log.debug("异步处理SIP请求成功: {}", processor.getProcessorName());
            } catch (Exception e) {
                log.error("异步处理SIP请求失败: {} - {}", processor.getProcessorName(), e.getMessage(), e);
                throw new SipException(SipErrorType.SYSTEM_INTERNAL, "ASYNC_PROCESS_ERROR",
                    "异步处理SIP请求失败: " + processor.getProcessorName(), e);
            }
        }, taskExecutor).orTimeout(asyncConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS)
            .exceptionally(throwable -> {
                if (throwable instanceof java.util.concurrent.TimeoutException) {
                    log.error("SIP异步处理超时: {} 超时时间: {}ms", processor.getProcessorName(), asyncConfig.getTimeoutMillis());
                } else {
                    log.error("SIP异步处理异常: {}", processor.getProcessorName(), throwable);
                }
                return null;
            });
    }

    /**
     * 异步执行SIP响应处理
     *
     * @param processor 异步处理器
     * @param responseEvent 响应事件
     * @return 异步处理结果
     */
    public CompletableFuture<Void> executeAsync(SipAsyncProcessor processor, ResponseEvent responseEvent) {
        if (!asyncConfig.isEnabled() || !processor.isAsyncSupported()) {
            // 如果异步未启用或处理器不支持异步，则同步执行
            return CompletableFuture.runAsync(() -> processor.processSync(responseEvent));
        }

        return CompletableFuture.runAsync(() -> {
            try {
                processor.processSync(responseEvent);
                log.debug("异步处理SIP响应成功: {}", processor.getProcessorName());
            } catch (Exception e) {
                log.error("异步处理SIP响应失败: {} - {}", processor.getProcessorName(), e.getMessage(), e);
                throw new SipException(SipErrorType.SYSTEM_INTERNAL, "ASYNC_PROCESS_ERROR",
                    "异步处理SIP响应失败: " + processor.getProcessorName(), e);
            }
        }, taskExecutor).orTimeout(asyncConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS)
            .exceptionally(throwable -> {
                if (throwable instanceof java.util.concurrent.TimeoutException) {
                    log.error("SIP异步处理超时: {} 超时时间: {}ms", processor.getProcessorName(), asyncConfig.getTimeoutMillis());
                } else {
                    log.error("SIP异步处理异常: {}", processor.getProcessorName(), throwable);
                }
                return null;
            });
    }

    /**
     * 提交任务到线程池
     *
     * @param task 任务
     * @return 异步结果
     */
    public CompletableFuture<Void> submit(Runnable task) {
        try {
            return CompletableFuture.runAsync(task, taskExecutor);
        } catch (RejectedExecutionException e) {
            log.error("任务提交被拒绝，线程池可能已满", e);
            throw new SipException(SipErrorType.RESOURCE_INSUFFICIENT, "TASK_REJECTED", "任务提交被拒绝", e);
        }
    }

    /**
     * 获取线程池状态信息
     */
    public String getPoolStatus() {
        ThreadPoolTaskExecutor executor = taskExecutor;
        return String.format("线程池状态 - 活跃线程: %d, 池大小: %d, 队列大小: %d, 完成任务: %d",
            executor.getActiveCount(),
            executor.getPoolSize(),
            executor.getThreadPoolExecutor().getQueue().size(),
            executor.getThreadPoolExecutor().getCompletedTaskCount());
    }

    /**
     * 获取底层执行器
     */
    public Executor getExecutor() {
        return taskExecutor;
    }

    @PreDestroy
    public void shutdown() {
        log.info("开始关闭SIP异步执行器...");
        taskExecutor.shutdown();
        log.info("SIP异步执行器关闭完成");
    }
}