package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.gbproxy.client.transmit.request.invite.InviteClientProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/11/7
 */
@Slf4j
@Component
public class DefaultInviteClientProcessorClient implements InviteClientProcessorClient {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;


    @Override
    public void inviteSession(SdpSessionDescription sessionDescription) {
        log.info("inviteSession::sessionDescription = {}", sessionDescription);
    }

    @Override
    public Device getToDevice(String userId) {
        return DefaultRegisterProcessorClient.deviceMap.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
