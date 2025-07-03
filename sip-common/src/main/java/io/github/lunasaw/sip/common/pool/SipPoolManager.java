package io.github.lunasaw.sip.common.pool;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * SIP连接池管理器
 * 负责连接池的定期维护、监控和清理工作
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
@Component
public class SipPoolManager {

    private final SipConnectionPool  connectionPool;
    private final SipPoolConfig      poolConfig;
    private ScheduledExecutorService scheduledExecutor;

    @Autowired
    public SipPoolManager(SipConnectionPool connectionPool, SipPoolConfig poolConfig) {
        this.connectionPool = connectionPool;
        this.poolConfig = poolConfig;
    }

    @PostConstruct
    public void initialize() {
        if (!poolConfig.isEnabled()) {
            log.info("SIP连接池未启用，跳过管理器初始化");
            return;
        }

        // 创建定时任务执行器
        scheduledExecutor = new ScheduledThreadPoolExecutor(2, r -> {
            Thread thread = new Thread(r, "sip-pool-manager");
            thread.setDaemon(true);
            return thread;
        });

        // 启动定期清理任务
        startCleanupTask();

        // 启动状态监控任务
        startMonitoringTask();

        log.info("SIP连接池管理器初始化完成");
    }

    /**
     * 启动清理任务
     */
    private void startCleanupTask() {
        long interval = poolConfig.getCleanupIntervalMillis();

        scheduledExecutor.scheduleWithFixedDelay(() -> {
            try {
                log.debug("执行SIP连接池清理任务");
                connectionPool.cleanupIdleConnections();
            } catch (Exception e) {
                log.error("SIP连接池清理任务异常", e);
            }
        }, interval, interval, TimeUnit.MILLISECONDS);

        log.info("SIP连接池清理任务已启动，间隔: {}ms", interval);
    }

    /**
     * 启动监控任务
     */
    private void startMonitoringTask() {
        if (!poolConfig.isEnableStatistics()) {
            log.info("SIP连接池统计功能未启用，跳过监控任务");
            return;
        }

        // 每5分钟输出一次连接池状态
        scheduledExecutor.scheduleWithFixedDelay(() -> {
            try {
                SipPoolStatus status = connectionPool.getPoolStatus();
                if (status.getTotalConnections() > 0) {
                    log.info("SIP连接池状态: {}", status.getSummary());

                    // 如果利用率过高，输出警告
                    if (status.getUtilizationRate() > 80) {
                        log.warn("SIP连接池利用率过高: {:.1f}%, 考虑增加最大连接数", status.getUtilizationRate());
                    }
                }
            } catch (Exception e) {
                log.error("SIP连接池监控任务异常", e);
            }
        }, 300000, 300000, TimeUnit.MILLISECONDS); // 5分钟间隔

        log.info("SIP连接池监控任务已启动");
    }

    /**
     * 获取连接池状态
     */
    public SipPoolStatus getPoolStatus() {
        return connectionPool.getPoolStatus();
    }

    /**
     * 获取详细状态报告
     */
    public String getDetailedStatusReport() {
        return connectionPool.getPoolStatus().getDetailedReport();
    }

    /**
     * 手动触发清理任务
     */
    public void triggerCleanup() {
        log.info("手动触发SIP连接池清理");
        connectionPool.cleanupIdleConnections();
    }

    /**
     * 释放指定地址的连接池
     */
    public void releasePool(String address, String transport) {
        log.info("释放SIP连接池: {}:{}", address, transport);
        connectionPool.releasePool(address, transport);
    }

    /**
     * 检查连接池健康状态
     */
    public boolean isHealthy() {
        try {
            SipPoolStatus status = connectionPool.getPoolStatus();

            // 检查连接数是否超过阈值
            if (status.getUtilizationRate() > 95) {
                log.warn("连接池利用率过高: {:.1f}%", status.getUtilizationRate());
                return false;
            }

            // 检查是否有异常的连接池
            for (SipPoolStatus.PoolEntry entry : status.getPoolEntries()) {
                entry.calculateUtilization();
                if (entry.getPoolUtilization() > 100) {
                    log.warn("发现异常连接池: {}, 利用率: {:.1f}%",
                        entry.getPoolKey(), entry.getPoolUtilization());
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            log.error("检查连接池健康状态异常", e);
            return false;
        }
    }

    /**
     * 获取配置信息
     */
    public String getConfigInfo() {
        return String.format("SIP连接池配置 - 启用: %b, 最大连接: %d, 核心连接: %d, 最大空闲: %d, 清理间隔: %dms",
            poolConfig.isEnabled(), poolConfig.getMaxConnections(), poolConfig.getCoreConnections(),
            poolConfig.getMaxIdleConnections(), poolConfig.getCleanupIntervalMillis());
    }

    @PreDestroy
    public void destroy() {
        if (scheduledExecutor != null && !scheduledExecutor.isShutdown()) {
            log.info("关闭SIP连接池管理器定时任务...");
            scheduledExecutor.shutdown();

            try {
                if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                    log.warn("强制关闭SIP连接池管理器定时任务");
                }
            } catch (InterruptedException e) {
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("SIP连接池管理器已关闭");
    }
}