package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.transmit.event.subscribe.SipSubscribeRequestProcessorAbstract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.transmit.event.subscribe.SubscribeHandlerAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP命令类型： 收到Subscribe请求
 *
 * @author luna
 */
@Component
@Getter
@Setter
@Slf4j
public class ClientSubscribeRequestProcessor extends SipSubscribeRequestProcessorAbstract {

    public static final String       METHOD = "SUBSCRIBE";

    private String                   method = METHOD;

    @Autowired
    private SubscribeProcessorClient subscribeProcessorClient;

    /**
     * 收到SUBSCRIBE请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

        SIPRequest request = (SIPRequest)evt.getRequest();

        // 在服务端看来 收到请求的时候fromHeader还是客户端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice)subscribeProcessorClient.getFromDevice();
        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

        doSubscribeHandForEvt(evt, fromDevice);
    }


}
