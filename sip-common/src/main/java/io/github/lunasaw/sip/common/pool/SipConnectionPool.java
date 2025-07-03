package io.github.lunasaw.sip.common.pool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.SipProviderImpl;
import io.github.lunasaw.sip.common.exception.SipConfigurationException;
import io.github.lunasaw.sip.common.exception.SipException;
import io.github.lunasaw.sip.common.exception.SipErrorType;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP连接池
 * 管理SIP连接的创建、复用和释放，提升资源利用效率
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
@Component
public class SipConnectionPool {

    private final SipPoolConfig                                     poolConfig;

    /**
     * 连接池映射：地址 -> 连接池
     */
    private final ConcurrentHashMap<String, SipConnectionPoolEntry> connectionPools  = new ConcurrentHashMap<>();

    /**
     * 全局连接计数器
     */
    private final AtomicInteger                                     totalConnections = new AtomicInteger(0);

    @Autowired
    public SipConnectionPool(SipPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        log.info("SIP连接池初始化 - 最大连接数: {}, 核心连接数: {}, 连接超时: {}ms",
            poolConfig.getMaxConnections(), poolConfig.getCoreConnections(), poolConfig.getConnectionTimeoutMillis());
    }

    /**
     * 获取SIP连接
     *
     * @param address 地址标识 (ip:port)
     * @param transport 传输协议 (UDP/TCP)
     * @return SIP提供者
     */
    public SipProviderImpl getConnection(String address, String transport) {
        String poolKey = buildPoolKey(address, transport);

        SipConnectionPoolEntry poolEntry = connectionPools.computeIfAbsent(poolKey,
            key -> new SipConnectionPoolEntry(address, transport));

        return poolEntry.borrowConnection();
    }

    /**
     * 归还SIP连接
     *
     * @param address 地址标识
     * @param transport 传输协议
     * @param provider SIP提供者
     */
    public void returnConnection(String address, String transport, SipProviderImpl provider) {
        String poolKey = buildPoolKey(address, transport);
        SipConnectionPoolEntry poolEntry = connectionPools.get(poolKey);

        if (poolEntry != null) {
            poolEntry.returnConnection(provider);
        } else {
            log.warn("尝试归还连接到不存在的连接池: {}", poolKey);
        }
    }

    /**
     * 释放指定地址的连接池
     *
     * @param address 地址标识
     * @param transport 传输协议
     */
    public void releasePool(String address, String transport) {
        String poolKey = buildPoolKey(address, transport);
        SipConnectionPoolEntry poolEntry = connectionPools.remove(poolKey);

        if (poolEntry != null) {
            poolEntry.destroy();
            log.info("释放SIP连接池: {}", poolKey);
        }
    }

    /**
     * 获取连接池状态信息
     */
    public SipPoolStatus getPoolStatus() {
        SipPoolStatus status = new SipPoolStatus();
        status.setTotalConnections(totalConnections.get());
        status.setTotalPools(connectionPools.size());
        status.setMaxConnections(poolConfig.getMaxConnections());

        connectionPools.forEach((key, pool) -> {
            SipPoolStatus.PoolEntry entry = new SipPoolStatus.PoolEntry();
            entry.setPoolKey(key);
            entry.setActiveConnections(pool.getActiveConnections());
            entry.setIdleConnections(pool.getIdleConnections());
            entry.setTotalBorrowed(pool.getTotalBorrowed());
            status.addPoolEntry(entry);
        });

        return status;
    }

    /**
     * 清理空闲连接
     */
    public void cleanupIdleConnections() {
        log.debug("开始清理空闲SIP连接...");

        connectionPools.values().forEach(pool -> {
            int cleaned = pool.cleanupIdleConnections();
            if (cleaned > 0) {
                log.debug("清理空闲连接: {} 个", cleaned);
            }
        });
    }

    /**
     * 构建连接池键值
     */
    private String buildPoolKey(String address, String transport) {
        return String.format("%s:%s", address, transport.toUpperCase());
    }

    @PreDestroy
    public void destroy() {
        log.info("开始销毁SIP连接池...");

        connectionPools.values().forEach(SipConnectionPoolEntry::destroy);
        connectionPools.clear();

        log.info("SIP连接池销毁完成，总连接数: {}", totalConnections.get());
    }

    /**
     * 连接池条目，管理单个地址的连接
     */
    private class SipConnectionPoolEntry {
        private final String                                 address;
        private final String                                 transport;
        private final ConcurrentLinkedQueue<SipProviderImpl> idleConnections   = new ConcurrentLinkedQueue<>();
        private final AtomicInteger                          activeConnections = new AtomicInteger(0);
        private final AtomicInteger                          totalBorrowed     = new AtomicInteger(0);
        private volatile boolean                             destroyed         = false;

        public SipConnectionPoolEntry(String address, String transport) {
            this.address = address;
            this.transport = transport;
        }

        /**
         * 借用连接
         */
        public SipProviderImpl borrowConnection() {
            if (destroyed) {
                throw new SipException(SipErrorType.RESOURCE_INSUFFICIENT, "POOL_DESTROYED",
                    "连接池已销毁: " + address);
            }

            // 检查全局连接数限制
            if (totalConnections.get() >= poolConfig.getMaxConnections()) {
                throw new SipException(SipErrorType.RESOURCE_INSUFFICIENT, "CONNECTION_LIMIT_EXCEEDED",
                    "已达到最大连接数限制: " + poolConfig.getMaxConnections());
            }

            // 尝试从空闲连接中获取
            SipProviderImpl provider = idleConnections.poll();
            if (provider != null && isConnectionValid(provider)) {
                activeConnections.incrementAndGet();
                totalBorrowed.incrementAndGet();
                log.debug("复用SIP连接: {}", address);
                return provider;
            }

            // 创建新连接
            provider = createNewConnection();
            if (provider != null) {
                activeConnections.incrementAndGet();
                totalConnections.incrementAndGet();
                totalBorrowed.incrementAndGet();
                log.debug("创建新SIP连接: {}", address);
            }

            return provider;
        }

        /**
         * 归还连接
         */
        public void returnConnection(SipProviderImpl provider) {
            if (destroyed || provider == null) {
                return;
            }

            if (isConnectionValid(provider) && idleConnections.size() < poolConfig.getMaxIdleConnections()) {
                idleConnections.offer(provider);
                log.debug("归还SIP连接到池: {}", address);
            } else {
                // 连接池已满或连接无效，直接关闭
                closeConnection(provider);
                totalConnections.decrementAndGet();
                log.debug("关闭多余SIP连接: {}", address);
            }

            activeConnections.decrementAndGet();
        }

        /**
         * 创建新连接
         */
        private SipProviderImpl createNewConnection() {
            try {
                // 这里应该根据实际需要创建SIP连接
                // 暂时返回null，需要集成到具体的SIP层实现中
                log.debug("创建SIP连接: {} 协议: {}", address, transport);
                return null; // 待实现
            } catch (Exception e) {
                throw new SipConfigurationException(address, "创建SIP连接失败", e);
            }
        }

        /**
         * 检查连接有效性
         */
        private boolean isConnectionValid(SipProviderImpl provider) {
            // 简单的有效性检查，实际应该根据SIP协议特性实现
            return provider != null;
        }

        /**
         * 关闭连接
         */
        private void closeConnection(SipProviderImpl provider) {
            try {
                if (provider != null) {
                    // 实际的连接关闭逻辑
                    log.debug("关闭SIP连接: {}", address);
                }
            } catch (Exception e) {
                log.error("关闭SIP连接异常: {}", address, e);
            }
        }

        /**
         * 清理空闲连接
         */
        public int cleanupIdleConnections() {
            int cleaned = 0;
            SipProviderImpl provider;

            while ((provider = idleConnections.poll()) != null) {
                if (!isConnectionValid(provider)) {
                    closeConnection(provider);
                    totalConnections.decrementAndGet();
                    cleaned++;
                } else {
                    // 连接仍然有效，放回队列
                    idleConnections.offer(provider);
                    break;
                }
            }

            return cleaned;
        }

        /**
         * 销毁连接池
         */
        public void destroy() {
            destroyed = true;

            SipProviderImpl provider;
            while ((provider = idleConnections.poll()) != null) {
                closeConnection(provider);
                totalConnections.decrementAndGet();
            }

            log.debug("销毁SIP连接池: {} 剩余活跃连接: {}", address, activeConnections.get());
        }

        public int getActiveConnections() {
            return activeConnections.get();
        }

        public int getIdleConnections() {
            return idleConnections.size();
        }

        public int getTotalBorrowed() {
            return totalBorrowed.get();
        }
    }
}