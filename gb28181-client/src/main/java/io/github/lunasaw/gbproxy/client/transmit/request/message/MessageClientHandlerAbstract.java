package io.github.lunasaw.gbproxy.client.transmit.request.message;

import javax.sip.RequestEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandlerAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;

/**
 * @author luna
 */
@Getter
@Component
@ConditionalOnBean(MessageProcessorClient.class)
public abstract class MessageClientHandlerAbstract extends MessageHandlerAbstract {

    @Autowired
    public MessageProcessorClient messageProcessorClient;

    @Autowired
    public SipUserGenerateClient  sipUserGenerate;

    public MessageClientHandlerAbstract(@Lazy MessageProcessorClient messageProcessorClient, SipUserGenerateClient sipUserGenerateClient) {
        this.messageProcessorClient = messageProcessorClient;
        this.sipUserGenerate = sipUserGenerateClient;
    }

    @Override
    public String getRootType() {
        return "Root";
    }

    public DeviceSession getDeviceSession(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest)event.getRequest();

        // 特别注意。这里的userId和sipId是反的，因为是客户端收到消息，所以这里的from是服务端，to是客户端
        String userId = SipUtils.getUserIdFromToHeader(sipRequest);
        // 客户端收到消息 fromHeader是服务端，toHeader是客户端
        String sipId = SipUtils.getUserIdFromFromHeader(sipRequest);

        return new DeviceSession(userId, sipId);
    }

}
