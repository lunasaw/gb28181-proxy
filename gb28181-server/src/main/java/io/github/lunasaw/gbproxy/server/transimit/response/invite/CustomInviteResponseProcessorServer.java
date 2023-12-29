package io.github.lunasaw.gbproxy.server.transimit.response.invite;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(InviteResponseProcessorServer.class)
public class CustomInviteResponseProcessorServer implements InviteResponseProcessorServer {
    @Override
    public void responseTrying() {

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
