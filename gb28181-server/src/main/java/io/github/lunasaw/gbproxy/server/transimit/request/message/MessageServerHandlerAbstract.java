package io.github.lunasaw.gbproxy.server.transimit.request.message;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.sip.common.entity.ToDevice;
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

    @Autowired
    @Lazy
    public MessageProcessorServer   messageProcessorServer;

    @Autowired
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

    public boolean preCheck(RequestEvent event) {
        if (!sipUserGenerate.checkDevice(event)) {
            return false;
        }
        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();
        // 设备查询
        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(userId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return false;
        }

        return true;
    }

}
