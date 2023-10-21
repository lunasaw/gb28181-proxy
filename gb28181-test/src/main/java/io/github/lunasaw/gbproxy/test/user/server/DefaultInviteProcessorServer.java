package io.github.lunasaw.gbproxy.test.user.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.response.invite.InviteProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;

/**
 * @author weidian
 * @date 2023/10/21
 */
@Component
public class DefaultInviteProcessorServer implements InviteProcessorServer {

    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;

    @Autowired
    @Qualifier("serverTo")
    private Device toDevice;

    @Override
    public Device getToDevice(String userId) {
        return toDevice;
    }

    @Override
    public Device getFromDevice(String userId) {
        return fromDevice;
    }
}
