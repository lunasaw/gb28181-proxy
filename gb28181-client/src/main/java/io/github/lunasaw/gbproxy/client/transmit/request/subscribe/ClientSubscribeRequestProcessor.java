package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
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

    @Autowired
    private SubscribeProcessorClient subscribeProcessorClient;

    @Autowired
    private SipUserGenerateClient    sipUserGenerateClient;

    public ClientSubscribeRequestProcessor(SubscribeProcessorClient subscribeProcessorClient, SipUserGenerateClient sipUserGenerateClient) {
        this.subscribeProcessorClient = subscribeProcessorClient;
        this.sipUserGenerateClient = sipUserGenerateClient;
    }

    /**
     * 收到SUBSCRIBE请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

        if (!sipUserGenerateClient.checkDevice(evt)) {
            // 如果是客户端收到的userId，一定是和自己的userId一致
            return;
        }

        doMessageHandForEvt(evt, (FromDevice)sipUserGenerateClient.getFromDevice());
    }

}
