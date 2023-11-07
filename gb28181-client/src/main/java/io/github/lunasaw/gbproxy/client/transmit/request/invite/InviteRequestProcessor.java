package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Response;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.sip.common.entity.*;
import io.github.lunasaw.sip.common.enums.ContentTypeEnum;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
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

        // 解析Sdp
        GbSessionDescription sessionDescription = (GbSessionDescription) SipUtils.parseSdp(new String(request.getRawContent()));
        inviteClientProcessorClient.inviteSession(sessionDescription);
        String conent = inviteClientProcessorClient.getAckContent(userId, sessionDescription);

        String receiveIp = request.getLocalAddress().getHostAddress();
        ContentTypeHeader contentTypeHeader = ContentTypeEnum.APPLICATION_SDP.getContentTypeHeader();
        ResponseCmd.doResponseCmd(Response.OK, "OK", receiveIp, conent, contentTypeHeader, request);
    }
}
