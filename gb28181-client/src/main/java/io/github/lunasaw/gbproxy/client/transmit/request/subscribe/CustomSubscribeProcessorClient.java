package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import io.github.lunasaw.gb28181.common.entity.query.DeviceQuery;
import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(SubscribeProcessorClient.class)
public class CustomSubscribeProcessorClient implements SubscribeProcessorClient {
    @Override
    public void putSubscribe(String userId, SubscribeInfo subscribeInfo) {

    }

    @Override
    public DeviceSubscribe getDeviceSubscribe(DeviceQuery deviceQuery) {
        return null;
    }

    @Override
    public Device getToDevice(String userId) {
        return null;
    }

    @Override
    public Device getFromDevice() {
        return null;
    }
}
