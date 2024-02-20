package io.github.lunasaw.gbproxy.client.transmit.request.bye;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.Dialog;
import javax.sip.RequestEvent;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;

/**
 * SIP命令类型： 收到Bye请求
 * 客户端发起Bye请求，结束通话
 *
 * @author luna
 */
@Component("byeRequestProcessorClient")
@Getter
@Setter
@Slf4j
public class ByeRequestProcessorClient extends SipRequestProcessorAbstract {

    public static final String METHOD = "BYE";

    private String method = METHOD;


    @Autowired
    private ByeProcessorClient byeProcessorClient;


    /**
     * 收到Bye请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        ResponseCmd.doResponseCmd(Response.OK, evt);
        Dialog dialog = evt.getDialog();
        if (dialog != null) {
            byeProcessorClient.closeStream(dialog.getCallId().getCallId());
        }
    }

}
