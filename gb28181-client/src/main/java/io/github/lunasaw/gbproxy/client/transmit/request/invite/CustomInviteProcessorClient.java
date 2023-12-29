package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(InviteProcessorClient.class)
public class CustomInviteProcessorClient implements InviteProcessorClient {
    @Override
    public void inviteSession(String callId, SdpSessionDescription sessionDescription) {

    }
}
