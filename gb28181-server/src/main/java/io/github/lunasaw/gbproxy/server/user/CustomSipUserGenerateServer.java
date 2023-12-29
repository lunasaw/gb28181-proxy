package io.github.lunasaw.gbproxy.server.user;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
public class CustomSipUserGenerateServer implements SipUserGenerateServer {
    @Override
    public Device getToDevice(String userId) {
        return null;
    }

    @Override
    public Device getFromDevice() {
        return null;
    }
}
