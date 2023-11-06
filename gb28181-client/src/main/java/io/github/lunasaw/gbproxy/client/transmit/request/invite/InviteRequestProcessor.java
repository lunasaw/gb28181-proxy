package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.utils.SipUtils;
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

    @Resource
    private InviteClientProcessorClient inviteClientProcessorClient;

    /**
     * 收到Invite请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice) inviteClientProcessorClient.getFromDevice();

        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

//        SipUtils.parseRequest()

//        inviteClientProcessorClient.handleInvite(evt);
    }

}
