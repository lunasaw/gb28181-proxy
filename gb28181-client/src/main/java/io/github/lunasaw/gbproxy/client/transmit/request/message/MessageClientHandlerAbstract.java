package io.github.lunasaw.gbproxy.client.transmit.request.message;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gb28181.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandlerAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

/**
 * @author luna
 */
@Data
@Component
@ConditionalOnBean(MessageProcessorClient.class)
public abstract class MessageClientHandlerAbstract extends MessageHandlerAbstract {

    @Resource
    public MessageProcessorClient messageProcessorClient;

    public MessageClientHandlerAbstract(@Lazy MessageProcessorClient messageProcessorClient) {
        this.messageProcessorClient = messageProcessorClient;
    }

    @Override
    public String getRootType() {
        return "Root";
    }

    public DeviceSession getDeviceSession(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();

        // 特别注意。这里的userId和sipId是反的，因为是客户端收到消息，所以这里的from是服务端，to是客户端
        String userId = SipUtils.getUserIdFromToHeader(sipRequest);
        // 客户端收到消息 fromHeader是服务端，toHeader是客户端
        String sipId = SipUtils.getUserIdFromFromHeader(sipRequest);

        return new DeviceSession(userId, sipId);
    }


}
