package io.github.lunasaw.gbproxy.test.user.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.response.invite.InviteResponseProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;

/**
 * @author weidian
 * @date 2023/10/21
 */
@Component
@Slf4j
public class DefaultInviteResponseProcessorServer implements InviteResponseProcessorServer {

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
    public Device getFromDevice() {
        return fromDevice;
    }

    @Override
    public void responseTrying() {
        log.info("responseTrying::");
    }
}
