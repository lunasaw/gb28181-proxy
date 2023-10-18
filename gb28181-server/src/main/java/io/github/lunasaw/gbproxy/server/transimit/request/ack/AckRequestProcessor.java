package io.github.lunasaw.gbproxy.server.transimit.request.ack;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

/**
 * SIP命令类型： 收到ACK请求
 * 被叫方收到INVITE请求后，回复200OK，然后发起ACK请求
 *
 * @author weidian
 */
@Component
@Getter
@Setter
public class AckRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "ACK";

    private String method = METHOD;

    /**
     * 收到ACK请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
