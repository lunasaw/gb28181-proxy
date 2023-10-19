package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP命令类型： 收到Invite请求
 * 客户端发起Invite请求, Invite Request消息实现，请求视频指令
 *
 * @author luna
 */
@Component
@Getter
@Setter
@Slf4j
public class InviteRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "INVITE";

    private String method = METHOD;

    /**
     * 收到Invite请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
