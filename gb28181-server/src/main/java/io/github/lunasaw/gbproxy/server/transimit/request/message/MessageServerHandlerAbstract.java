package io.github.lunasaw.gbproxy.server.transimit.request.message;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.DeviceSession;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandlerAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Data;

/**
 * @author luna
 */
@Data
@Component
public abstract class MessageServerHandlerAbstract extends MessageHandlerAbstract {

    @Resource
    @Lazy
    public MessageProcessorServer   messageProcessorServer;

    @Resource
    protected SipUserGenerateServer sipUserGenerate;

    public MessageServerHandlerAbstract(MessageProcessorServer messageProcessorServer, SipUserGenerateServer sipUserGenerate) {
        this.messageProcessorServer = messageProcessorServer;
        this.sipUserGenerate = sipUserGenerate;
    }

    @Override
    public String getRootType() {
        return "Root";
    }

    public DeviceSession getDeviceSession(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest)event.getRequest();

        // 客户端发送的userId
        String userId = SipUtils.getUserIdFromFromHeader(sipRequest);
        // 服务端接收的userId
        // 服务端收到消息，fromHeader是服务端的userId
        String sipId = SipUtils.getUserIdFromToHeader(sipRequest);

        return new DeviceSession(userId, sipId);
    }

}
