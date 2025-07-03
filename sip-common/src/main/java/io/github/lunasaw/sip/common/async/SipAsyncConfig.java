package io.github.lunasaw.sip.common.async;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * SIP异步处理配置
 * 配置异步处理相关参数
 *
 * @author luna
 * @date 2024/01/01
 */
@Data
@Component
@ConfigurationProperties(prefix = "sip.async")
public class SipAsyncConfig {

    /**
     * 是否启用异步处理
     */
    private boolean enabled                          = true;

    /**
     * 核心线程数
     */
    private int     corePoolSize                     = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数
     */
    private int     maxPoolSize                      = corePoolSize * 2;

    /**
     * 线程空闲时间（秒）
     */
    private int     keepAliveSeconds                 = 60;

    /**
     * 队列容量
     */
    private int     queueCapacity                    = 1000;

    /**
     * 线程名前缀
     */
    private String  threadNamePrefix                 = "sip-async-";

    /**
     * 异步处理超时时间（毫秒）
     */
    private long    timeoutMillis                    = 30000L;

    /**
     * 是否等待任务完成后关闭
     */
    private boolean waitForTasksToCompleteOnShutdown = true;

    /**
     * 等待关闭时间（秒）
     */
    private int     awaitTerminationSeconds          = 10;

    /**
     * 是否拒绝策略使用调用者线程执行
     */
    private boolean callerRunsPolicy                 = true;
}