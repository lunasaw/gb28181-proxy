package io.github.lunasaw.gbproxy.server.transimit.request.notify;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.RequestEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandlerAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Data;

/**
 * @author luna
 */
@Data
@Component
public abstract class NotifyServerHandlerAbstract extends MessageHandlerAbstract {

    @Autowired
    public NotifyProcessorServer    notifyProcessorServer;

    @Autowired
    protected SipUserGenerateServer sipUserGenerate;

    @Autowired
    public void setNotifyProcessorServer(NotifyProcessorServer notifyProcessorServer) {
        this.notifyProcessorServer = notifyProcessorServer;
    }

    @Autowired
    public void setSipUserGenerate(SipUserGenerateServer sipUserGenerate) {
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
