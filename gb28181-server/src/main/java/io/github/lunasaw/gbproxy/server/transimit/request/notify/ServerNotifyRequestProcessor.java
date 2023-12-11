package io.github.lunasaw.gbproxy.server.transimit.request.notify;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
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
public class ServerNotifyRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "NOTIFY";

    private String             method = METHOD;

    /**
     * 收到Notify请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
