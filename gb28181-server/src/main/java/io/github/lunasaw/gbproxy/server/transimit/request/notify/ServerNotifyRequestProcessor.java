package io.github.lunasaw.gbproxy.server.transimit.request.notify;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.message.SipMessageRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * SIP命令类型： 收到Notify请求
 *
 * @author luna
 */
@Component
@Getter
@Setter
public class ServerNotifyRequestProcessor extends SipMessageRequestProcessorAbstract {

    public static final String    METHOD = "NOTIFY";

    private String                method = METHOD;

    @Resource
    private NotifyProcessorServer notifyProcessorServer;

    @Resource
    private SipUserGenerateServer sipUserGenerate;

    /**
     * 收到Notify请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        if (!sipUserGenerate.checkDevice(evt)) {
            // 如果是客户端收到的userId，一定是和自己的userId一致
            return;
        }

        doMessageHandForEvt(evt, (FromDevice) sipUserGenerate.getFromDevice());
    }

}
