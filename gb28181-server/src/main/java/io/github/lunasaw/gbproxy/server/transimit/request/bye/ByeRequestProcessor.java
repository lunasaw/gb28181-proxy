package io.github.lunasaw.gbproxy.server.transimit.request.bye;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

/**
 * SIP命令类型： 收到Bye请求
 * 客户端发起Bye请求，结束通话
 * @author luna
 */
@Component
@Getter
@Setter
public class ByeRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "BYE";

    private String method = METHOD;

    /**
     * 收到Bye请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
