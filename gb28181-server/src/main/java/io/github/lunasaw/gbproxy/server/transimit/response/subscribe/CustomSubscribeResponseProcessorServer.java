package io.github.lunasaw.gbproxy.server.transimit.response.subscribe;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(SubscribeResponseProcessorServer.class)
public class CustomSubscribeResponseProcessorServer implements SubscribeResponseProcessorServer {
    @Override
    public void subscribeResult(DeviceSubscribe deviceSubscribe) {

    }
}
