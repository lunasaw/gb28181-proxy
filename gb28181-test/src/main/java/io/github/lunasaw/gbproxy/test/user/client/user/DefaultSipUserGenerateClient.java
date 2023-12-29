package io.github.lunasaw.gbproxy.test.user.client.user;

import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.entity.Device;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
public class DefaultSipUserGenerateClient implements SipUserGenerateClient {

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
