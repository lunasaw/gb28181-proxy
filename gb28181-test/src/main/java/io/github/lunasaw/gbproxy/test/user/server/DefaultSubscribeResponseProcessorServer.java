package io.github.lunasaw.gbproxy.test.user.server;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.response.subscribe.SubscribeResponseProcessorServer;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.response.DeviceSubscribe;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/11
 * @description:
 */
@Component
@Slf4j
public class DefaultSubscribeResponseProcessorServer implements SubscribeResponseProcessorServer {

    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;

    @Override
    public void subscribeResult(DeviceSubscribe deviceSubscribe) {
        log.info("收到订阅消息响应 subscribeResult::deviceSubscribe = {} ", JSON.toJSONString(deviceSubscribe));
    }

    @Override
    public Device getToDevice(String userId) {
        return DeviceConfig.DEVICE_SERVER_VIEW_MAP.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
