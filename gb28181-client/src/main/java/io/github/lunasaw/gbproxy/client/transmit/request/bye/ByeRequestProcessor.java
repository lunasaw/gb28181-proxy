package io.github.lunasaw.gbproxy.client.transmit.request.bye;

import javax.sip.RequestEvent;

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
@Component("clientRequestProcessor")
@Getter
@Setter
@Slf4j
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
