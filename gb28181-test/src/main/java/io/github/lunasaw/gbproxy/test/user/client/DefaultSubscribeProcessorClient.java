package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.gbproxy.client.transmit.request.subscribe.SubscribeProcessorClient;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author weidian
 * @version 1.0
 * @date 2023/12/11
 * @description:
 */
@Component
public class DefaultSubscribeProcessorClient implements SubscribeProcessorClient {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Override
    public Device getToDevice(String userId) {
        return DeviceConfig.DEVICE_CLIENT_VIEW_MAP.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
