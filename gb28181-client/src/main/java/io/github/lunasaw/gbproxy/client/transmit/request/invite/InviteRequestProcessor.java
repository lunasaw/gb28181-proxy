package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import io.github.lunasaw.sip.common.enums.ContentTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.GbSessionDescription;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.transmit.event.SipMethod;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * INVITE请求处理器
 * 处理客户端接收到的INVITE请求
 *
 * @author luna
 */
@SipMethod("INVITE")
@Component
@Getter
@Setter
@Slf4j
public class InviteRequestProcessor extends SipRequestProcessorAbstract {

    @Autowired
    private InviteProcessorClient inviteProcessorClient;

    @Autowired
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
