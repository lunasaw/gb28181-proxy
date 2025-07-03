package io.github.lunasaw.gbproxy.client.transmit.request.message;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.SipMethod;
import io.github.lunasaw.sip.common.transmit.event.message.SipMessageRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * MESSAGE请求处理器
 * 处理客户端接收到的MESSAGE请求
 *
 * @author luna
 */
@SipMethod("MESSAGE")
@Component
@Getter
@Setter
@Slf4j
public class ClientMessageRequestProcessor extends SipMessageRequestProcessorAbstract {

    @Autowired
    private MessageProcessorClient messageProcessorClient;

    @Autowired
    private SipUserGenerateClient  sipUserGenerate;

    @Override
    public void process(RequestEvent evt) {
        if (!sipUserGenerate.checkDevice(evt)) {
            // 如果是客户端收到的userId，一定是和自己的userId一致
            return;
        }

        doMessageHandForEvt(evt, (FromDevice)sipUserGenerate.getFromDevice());
    }
}
