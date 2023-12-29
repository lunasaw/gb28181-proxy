package io.github.lunasaw.gbproxy.client.transmit.request.ack;

import javax.annotation.Resource;
import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP命令类型： ACK请求
 * 
 * @author weidian
 */
@Component
@Getter
@Setter
@Slf4j
public class ClientAckRequestProcessor extends SipRequestProcessorAbstract {

    private final String              METHOD = "ACK";

    private String                    method = METHOD;

    @Resource
    private AckRequestProcessorClient ackRequestProcessorClient;

    @Resource
    private SipUserGenerateClient     sipUserGenerate;

    /**
     * 处理 ACK请求
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        if (!sipUserGenerate.checkDevice(evt)) {
            return;
        }
        Dialog dialog = evt.getDialog();
        if (dialog == null) {
            return;
        }
        if (dialog.getState() == DialogState.CONFIRMED) {
            SipSubscribe.publishAckEvent(evt);
        }
    }

}
