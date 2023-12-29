package io.github.lunasaw.gbproxy.client.transmit.request.ack;


import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.Dialog;
import javax.sip.DialogState;
import javax.sip.RequestEvent;

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

    private final String METHOD = "ACK";

    private String method = METHOD;

    @Resource
    private AckRequestProcessorClient ackRequestProcessorClient;

    @Resource
    private SipUserGenerate           sipUserGenerate;

    /**
     * 处理  ACK请求
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();

        if (!userId.equals(fromDevice.getUserId())) {
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
