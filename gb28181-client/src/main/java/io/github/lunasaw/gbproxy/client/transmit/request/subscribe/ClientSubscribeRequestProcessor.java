package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import io.github.lunasaw.sip.common.transmit.event.message.SipMessageRequestProcessorAbstract;
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
public class ClientSubscribeRequestProcessor extends SipMessageRequestProcessorAbstract {

    public static final String       METHOD = "SUBSCRIBE";

    private String                   method = METHOD;

    @Resource
    private SubscribeProcessorClient subscribeProcessorClient;

    @Resource
    private SipUserGenerate          sipUserGenerate;

    public ClientSubscribeRequestProcessor(SubscribeProcessorClient subscribeProcessorClient, SipUserGenerate sipUserGenerate) {
        this.subscribeProcessorClient = subscribeProcessorClient;
        this.sipUserGenerate = sipUserGenerate;
    }

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
        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

        doMessageHandForEvt(evt, fromDevice);
    }

}
