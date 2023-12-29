package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import javax.sip.RequestEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gb28181.common.entity.base.DeviceSession;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandlerAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Data;

/**
 * @author luna
 */
@Data
@Component
public abstract class SubscribeClientHandlerAbstract extends MessageHandlerAbstract {

    @Autowired
    protected SubscribeProcessorClient subscribeProcessorClient;

    @Autowired
    protected SipUserGenerateClient    sipUserGenerate;

    public SubscribeClientHandlerAbstract(SubscribeProcessorClient subscribeProcessorClient, SipUserGenerateClient sipUserGenerate) {
        this.subscribeProcessorClient = subscribeProcessorClient;
        this.sipUserGenerate = sipUserGenerate;
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
