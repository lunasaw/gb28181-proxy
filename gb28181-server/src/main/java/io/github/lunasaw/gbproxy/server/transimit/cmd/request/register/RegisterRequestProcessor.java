package io.github.lunasaw.gbproxy.server.transimit.cmd.request.register;

import javax.sip.RequestEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;

/**
 * SIP命令类型： REGISTER请求
 *
 * @author weidian
 */
@Component
public class RegisterRequestProcessor extends SipRequestProcessorAbstract {

    public final String method = "REGISTER";

    /**
     * 收到注册请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
