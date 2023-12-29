package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.GbSessionDescription;
import io.github.lunasaw.sip.common.enums.ContentTypeEnum;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
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

    public static final String    METHOD = "INVITE";

    private String                method = METHOD;

    @Resource
    private InviteProcessorClient inviteProcessorClient;

    @Resource
    private SipUserGenerateClient sipUserGenerate;

    /**
     * 收到Invite请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest)evt.getRequest();

        if (!sipUserGenerate.checkDevice(evt)) {
            return;
        }
        String toUserId = SipUtils.getUserIdFromFromHeader(request);
        Device toDevice = sipUserGenerate.getToDevice(toUserId);
        if (toDevice == null) {
            return;
        }

        String userId = SipUtils.getUserIdFromToHeader(request);
        String callId = SipUtils.getCallId(request);
        // 解析Sdp
        GbSessionDescription sessionDescription = (GbSessionDescription)SipUtils.parseSdp(new String(request.getRawContent()));
        inviteProcessorClient.inviteSession(callId, sessionDescription);
        String content = inviteProcessorClient.getInviteResponse(userId, sessionDescription);

        ContentTypeHeader contentTypeHeader = ContentTypeEnum.APPLICATION_SDP.getContentTypeHeader();
        ResponseCmd.doResponseCmd(Response.OK, "OK", content, contentTypeHeader, evt);
    }
}
