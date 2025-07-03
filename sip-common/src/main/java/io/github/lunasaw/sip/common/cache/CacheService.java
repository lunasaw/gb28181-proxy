package io.github.lunasaw.sip.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 缓存服务类 - 统一管理Caffeine缓存操作
 *
 * @author luna
 * @date 2024/1/6
 */
@Slf4j
@Service
public class CacheService {

    private final Cache<String, Object> deviceCache;
    private final Cache<String, Object> subscribeCache;
    private final Cache<String, Object> transactionCache;
    private final Cache<String, Object> sipMessageCache;

    public CacheService(
            @Qualifier("deviceCaffeine") Cache<String, Object> deviceCache,
            @Qualifier("subscribeCaffeine") Cache<String, Object> subscribeCache,
            @Qualifier("transactionCaffeine") Cache<String, Object> transactionCache,
            @Qualifier("sipMessageCaffeine") Cache<String, Object> sipMessageCache) {
        this.deviceCache = deviceCache;
        this.subscribeCache = subscribeCache;
        this.transactionCache = transactionCache;
        this.sipMessageCache = sipMessageCache;
    }

    // ==================== 设备缓存操作 ====================
    
    /**
     * 获取设备信息
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getDevice(String deviceId, Class<T> type) {
        try {
            Object value = deviceCache.getIfPresent(deviceId);
            if (value != null && type.isInstance(value)) {
                return Optional.of((T) value);
            }
        } catch (Exception e) {
            log.warn("Failed to get device from cache: {}", deviceId, e);
        }
        return Optional.empty();
    }

    /**
     * 存储设备信息
     */
    public void putDevice(String deviceId, Object device) {
        try {
            deviceCache.put(deviceId, device);
            log.debug("Device cached: {}", deviceId);
        } catch (Exception e) {
            log.warn("Failed to cache device: {}", deviceId, e);
        }
    }

    /**
     * 移除设备信息
     */
    public void removeDevice(String deviceId) {
        try {
            deviceCache.invalidate(deviceId);
            log.debug("Device removed from cache: {}", deviceId);
        } catch (Exception e) {
            log.warn("Failed to remove device from cache: {}", deviceId, e);
        }
    }

    // ==================== 订阅缓存操作 ====================
    
    /**
     * 获取订阅信息
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getSubscribe(String subscribeId, Class<T> type) {
        try {
            Object value = subscribeCache.getIfPresent(subscribeId);
            if (value != null && type.isInstance(value)) {
                return Optional.of((T) value);
            }
        } catch (Exception e) {
            log.warn("Failed to get subscribe from cache: {}", subscribeId, e);
        }
        return Optional.empty();
    }

    /**
     * 存储订阅信息
     */
    public void putSubscribe(String subscribeId, Object subscribe) {
        try {
            subscribeCache.put(subscribeId, subscribe);
            log.debug("Subscribe cached: {}", subscribeId);
        } catch (Exception e) {
            log.warn("Failed to cache subscribe: {}", subscribeId, e);
        }
    }

    /**
     * 移除订阅信息
     */
    public void removeSubscribe(String subscribeId) {
        try {
            subscribeCache.invalidate(subscribeId);
            log.debug("Subscribe removed from cache: {}", subscribeId);
        } catch (Exception e) {
            log.warn("Failed to remove subscribe from cache: {}", subscribeId, e);
        }
    }

    // ==================== 事务缓存操作 ====================
    
    /**
     * 获取事务信息
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getTransaction(String transactionId, Class<T> type) {
        try {
            Object value = transactionCache.getIfPresent(transactionId);
            if (value != null && type.isInstance(value)) {
                return Optional.of((T) value);
            }
        } catch (Exception e) {
            log.warn("Failed to get transaction from cache: {}", transactionId, e);
        }
        return Optional.empty();
    }

    /**
     * 存储事务信息
     */
    public void putTransaction(String transactionId, Object transaction) {
        try {
            transactionCache.put(transactionId, transaction);
            log.debug("Transaction cached: {}", transactionId);
        } catch (Exception e) {
            log.warn("Failed to cache transaction: {}", transactionId, e);
        }
    }

    /**
     * 移除事务信息
     */
    public void removeTransaction(String transactionId) {
        try {
            transactionCache.invalidate(transactionId);
            log.debug("Transaction removed from cache: {}", transactionId);
        } catch (Exception e) {
            log.warn("Failed to remove transaction from cache: {}", transactionId, e);
        }
    }

    // ==================== SIP消息缓存操作 ====================
    
    /**
     * 获取SIP消息
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getSipMessage(String messageId, Class<T> type) {
        try {
            Object value = sipMessageCache.getIfPresent(messageId);
            if (value != null && type.isInstance(value)) {
                return Optional.of((T) value);
            }
        } catch (Exception e) {
            log.warn("Failed to get SIP message from cache: {}", messageId, e);
        }
        return Optional.empty();
    }

    /**
     * 存储SIP消息
     */
    public void putSipMessage(String messageId, Object message) {
        try {
            sipMessageCache.put(messageId, message);
            log.debug("SIP message cached: {}", messageId);
        } catch (Exception e) {
            log.warn("Failed to cache SIP message: {}", messageId, e);
        }
    }

    /**
     * 移除SIP消息
     */
    public void removeSipMessage(String messageId) {
        try {
            sipMessageCache.invalidate(messageId);
            log.debug("SIP message removed from cache: {}", messageId);
        } catch (Exception e) {
            log.warn("Failed to remove SIP message from cache: {}", messageId, e);
        }
    }

    // ==================== 缓存统计 ====================
    
    /**
     * 获取缓存统计信息
     */
    public String getCacheStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("Device Cache Stats: ").append(deviceCache.stats()).append("\n");
        stats.append("Subscribe Cache Stats: ").append(subscribeCache.stats()).append("\n");
        stats.append("Transaction Cache Stats: ").append(transactionCache.stats()).append("\n");
        stats.append("SIP Message Cache Stats: ").append(sipMessageCache.stats()).append("\n");
        return stats.toString();
    }

    /**
     * 清空所有缓存
     */
    public void clearAllCaches() {
        try {
            deviceCache.invalidateAll();
            subscribeCache.invalidateAll();
            transactionCache.invalidateAll();
            sipMessageCache.invalidateAll();
            log.info("All caches cleared");
        } catch (Exception e) {
            log.error("Failed to clear caches", e);
        }
    }
}