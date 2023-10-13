package io.github.lunasaw.gbproxy.common.transmit.cmd;

import java.util.List;

import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;

import org.assertj.core.util.Lists;

import io.github.lunasaw.gbproxy.common.entity.FromDevice;
import io.github.lunasaw.gbproxy.common.entity.SipMessage;
import io.github.lunasaw.gbproxy.common.entity.ToDevice;
import io.github.lunasaw.gbproxy.common.utils.SipRequestUtils;

/**
 * Sip命令request创造器
 */
public class SIPRequestHeaderProvider {

    /**
     * 创建SIP请求
     * 
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param sipMessage 内容
     * @return Request
     */
    public static Request createSipRequest(FromDevice fromDevice, ToDevice toDevice, SipMessage sipMessage) {

        CallIdHeader callIdHeader = SipRequestUtils.createCallIdHeader(sipMessage.getCallId());
        // sipUri
        SipURI requestUri = SipRequestUtils.createSipUri(toDevice.getUserId(), toDevice.getHostAddress());
        // via
        ViaHeader viaHeader =
            SipRequestUtils.createViaHeader(fromDevice.getIp(), fromDevice.getPort(), toDevice.getTransport(), sipMessage.getViaTag());
        List<ViaHeader> viaHeaders = Lists.newArrayList(viaHeader);
        // from
        FromHeader fromHeader = SipRequestUtils.createFromHeader(fromDevice.getUserId(), fromDevice.getHostAddress(), fromDevice.getFromTag());
        // to
        ToHeader toHeader = SipRequestUtils.createToHeader(toDevice.getUserId(), toDevice.getHostAddress(), toDevice.getToTag());
        // Forwards
        MaxForwardsHeader maxForwards = SipRequestUtils.createMaxForwardsHeader();
        // ceq
        CSeqHeader cSeqHeader = SipRequestUtils.createCSeqHeader(sipMessage.getSequence(), sipMessage.getMethod());
        // request
        Request request = SipRequestUtils.createRequest(requestUri, sipMessage.getMethod(), callIdHeader, cSeqHeader, fromHeader,
            toHeader, viaHeaders, maxForwards, sipMessage.getContentTypeHeader(), sipMessage.getContent());

        SipRequestUtils.setRequestHeader(request, sipMessage.getHeaders());
        return request;
    }

    /**
     * 创建Message请求
     * 
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param content 内容
     * @param callId callId
     * @return Request
     */
    public static Request createMessageRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId) {
        SipMessage sipMessage = SipMessage.getMessageBody();
        sipMessage.setMethod(Request.MESSAGE);
        sipMessage.setContent(content);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        sipMessage.addHeader(userAgentHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

    /**
     * 创建Invite请求
     *
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param content 内容
     * @param callId callId
     * @return Request
     */
    public static Request createInviteRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId) {
        SipMessage sipMessage = SipMessage.getInviteBody();
        sipMessage.setMethod(Request.INVITE);
        sipMessage.setContent(content);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());
        SubjectHeader subjectHeader = SipRequestUtils.createSubjectHeader(toDevice.getSubject());

        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader).addHeader(subjectHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

    public Request createPlaybackInviteRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId) {
        return createInviteRequest(fromDevice, toDevice, content, callId);
    }

    /**
     * 创建Bye请求
     * 
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param callId callId
     * @return Request
     */
    public static Request createByeRequest(FromDevice fromDevice, ToDevice toDevice, String callId) {

        SipMessage sipMessage = SipMessage.getByeBody();
        sipMessage.setMethod(Request.INVITE);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());

        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());
        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

    /**
     * 创建Subscribe请求
     * 
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param content 内容
     * @param callId callId
     * @param expires 过期时间
     * @param event 事件名称
     * @return Request
     */
    public static Request createSubscribeRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId, Integer expires, String event) {
        SipMessage sipMessage = SipMessage.getSubscribeBody();
        sipMessage.setMethod(Request.SUBSCRIBE);
        sipMessage.setContent(content);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());
        ExpiresHeader expiresHeader = SipRequestUtils.createExpiresHeader(expires);
        EventHeader eventHeader = SipRequestUtils.createEventHeader(event);
        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader).addHeader(expiresHeader).addHeader(eventHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

    /**
     * 创建INFO 请求
     * 
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param content 内容
     * @param callId callId
     * @return Request
     */
    public static Request createInfoRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId) {

        SipMessage sipMessage = SipMessage.getInfoBody();
        sipMessage.setMethod(Request.INFO);
        sipMessage.setContent(content);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());

        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

    /**
     * 创建ACK请求
     * 
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param callId callId
     * @return Request
     */
    public static Request createAckRequest(FromDevice fromDevice, ToDevice toDevice, String callId) {
        SipMessage sipMessage = SipMessage.getAckBody();
        sipMessage.setMethod(Request.ACK);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());

        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());
        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }
}
