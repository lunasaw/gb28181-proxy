package io.github.lunasaw.sip.common.subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.utils.DynamicTask;

/**
 * @author lin
 */
@Component
public class SubscribeHolder {

    private static ConcurrentHashMap<String, SubscribeInfo> catalogMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, SubscribeInfo> mobilePositionMap = new ConcurrentHashMap<>();
    private final String taskOverduePrefix = "subscribe_overdue_";
    @Autowired
    private DynamicTask dynamicTask;

    public void putCatalogSubscribe(String userId, SubscribeInfo subscribeInfo) {
        catalogMap.put(userId, subscribeInfo);
        // 添加订阅到期
        String taskOverdueKey = taskOverduePrefix + "catalog_" + userId;
        // 添加任务处理订阅过期
        dynamicTask.startDelay(taskOverdueKey, () -> removeCatalogSubscribe(subscribeInfo.getId()),
                subscribeInfo.getExpires() * 1000);
    }

    public SubscribeInfo getCatalogSubscribe(String platformId) {
        return catalogMap.get(platformId);
    }

    public void removeCatalogSubscribe(String sipId) {

        catalogMap.remove(sipId);
        String taskOverdueKey = taskOverduePrefix + "catalog_" + sipId;
        Runnable runnable = dynamicTask.get(taskOverdueKey);
        if (runnable instanceof SubscribeTask) {
            SubscribeTask subscribeTask = (SubscribeTask) runnable;
            subscribeTask.stop();
        }
        // 添加任务处理订阅过期
        dynamicTask.stop(taskOverdueKey);
    }

    public void putMobilePositionSubscribe(String userId, String prefixKey, SubscribeTask task, SubscribeInfo subscribeInfo) {
        mobilePositionMap.put(userId, subscribeInfo);
        String key = prefixKey + userId;
        // 添加任务处理GPS定时推送
        dynamicTask.startCron(key, task, subscribeInfo.getGpsInterval() * 1000);
        String taskOverdueKey = taskOverduePrefix + prefixKey + userId;
        // 添加任务处理订阅过期
        dynamicTask.startDelay(taskOverdueKey, () -> {
                    removeMobilePositionSubscribe(subscribeInfo.getId(), prefixKey);
                },
                subscribeInfo.getExpires() * 1000);
    }

    public SubscribeInfo getMobilePositionSubscribe(String userId) {
        return mobilePositionMap.get(userId);
    }

    public void removeMobilePositionSubscribe(String userId, String prefixKey) {
        mobilePositionMap.remove(userId);
        String key = prefixKey + "MobilePosition_" + userId;
        // 结束任务处理GPS定时推送
        dynamicTask.stop(key);
        String taskOverdueKey = taskOverduePrefix + "MobilePosition_" + userId;
        Runnable runnable = dynamicTask.get(taskOverdueKey);
        if (runnable instanceof SubscribeTask) {
            SubscribeTask subscribeTask = (SubscribeTask) runnable;
            subscribeTask.stop();
        }
        // 添加任务处理订阅过期
        dynamicTask.stop(taskOverdueKey);
    }

    public List<String> getAllCatalogSubscribePlatform() {
        List<String> platforms = new ArrayList<>();
        if (MapUtils.isNotEmpty(catalogMap)) {
            for (String key : catalogMap.keySet()) {
                platforms.add(catalogMap.get(key).getId());
            }
        }
        return platforms;
    }

    public void removeAllSubscribe(String userId, String prefixKey) {
        removeMobilePositionSubscribe(userId, prefixKey);
        removeCatalogSubscribe(userId);
    }
}
