package io.github.lunasaw.gbproxy.test.user.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;
import io.github.lunasaw.gbproxy.server.transimit.response.subscribe.SubscribeResponseProcessorServer;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import lombok.extern.slf4j.Slf4j;

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
}
