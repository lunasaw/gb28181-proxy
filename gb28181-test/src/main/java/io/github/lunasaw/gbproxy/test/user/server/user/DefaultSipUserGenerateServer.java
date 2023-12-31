package io.github.lunasaw.gbproxy.test.user.server.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
public class DefaultSipUserGenerateServer implements SipUserGenerateServer {

    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;

    @Override
    public Device getToDevice(String userId) {
        return DeviceConfig.DEVICE_SERVER_VIEW_MAP.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
