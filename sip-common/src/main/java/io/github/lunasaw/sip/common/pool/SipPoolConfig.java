package io.github.lunasaw.sip.common.pool;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * SIP连接池配置
 * 定义连接池的各种参数和行为
 *
 * @author luna
 * @date 2024/01/01
 */
@Data
@Component
@ConfigurationProperties(prefix = "sip.pool")
public class SipPoolConfig {

    /**
     * 是否启用连接池
     */
    private boolean enabled                  = true;

    /**
     * 最大连接数
     */
    private int     maxConnections           = 100;

    /**
     * 核心连接数（预创建的连接数）
     */
    private int     coreConnections          = 10;

    /**
     * 每个地址的最大空闲连接数
     */
    private int     maxIdleConnections       = 5;

    /**
     * 连接超时时间（毫秒）
     */
    private long    connectionTimeoutMillis  = 30000L;

    /**
     * 连接空闲超时时间（毫秒）
     */
    private long    idleTimeoutMillis        = 300000L;

    /**
     * 连接验证间隔（毫秒）
     */
    private long    validationIntervalMillis = 60000L;

    /**
     * 是否在借用时验证连接
     */
    private boolean testOnBorrow             = true;

    /**
     * 是否在归还时验证连接
     */
    private boolean testOnReturn             = false;

    /**
     * 是否在空闲时验证连接
     */
    private boolean testWhileIdle            = true;

    /**
     * 清理任务执行间隔（毫秒）
     */
    private long    cleanupIntervalMillis    = 120000L;

    /**
     * 是否启用连接统计
     */
    private boolean enableStatistics         = true;

    /**
     * 是否在关闭时强制清理所有连接
     */
    private boolean forceCleanupOnShutdown   = true;

    /**
     * 最大等待时间（毫秒）
     */
    private long    maxWaitMillis            = 10000L;

    /**
     * 验证连接有效性时调用的方法参数
     */
    private String  validationQuery          = "OPTIONS";
}