package io.github.lunasaw.gbproxy.client.transmit.request.bye;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.transmit.request.invite.InviteProcessorClient;
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
@Component("clientByeRequestProcessor")
@Getter
@Setter
@Slf4j
public class ByeRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "BYE";

    private String method = METHOD;


    @Resource
    private InviteProcessorClient inviteProcessorClient;


    /**
     * 收到Bye请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
