package io.github.lunasaw.sip.common.transmit.request;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.Request;

import org.assertj.core.util.Lists;
import org.springframework.util.DigestUtils;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.SipMessage;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;

/**
 * Sip命令request创造器
 */
public class SipRequestProvider {

    /**
     * 带订阅创建SIP请求
     *
     * @param fromDevice    发送设备
     * @param toDevice      发送目的设备
     * @param sipMessage    内容
     * @param subscribeInfo 订阅消息
     * @return
     */
    public static Request createSipRequest(FromDevice fromDevice, ToDevice toDevice, SipMessage sipMessage, SubscribeInfo subscribeInfo) {
        if (subscribeInfo != null) {

            Optional.ofNullable(subscribeInfo.getRequest()).map(SIPRequest::getCallIdHeader).map(CallIdHeader::getCallId)
                    .ifPresent(sipMessage::setCallId);
            Optional.ofNullable(subscribeInfo.getResponse()).map(SIPResponse::getToTag).ifPresent(fromDevice::setFromTag);
            Optional.ofNullable(subscribeInfo.getRequest()).map(SIPRequest::getFromTag).ifPresent(toDevice::setToTag);

            if (subscribeInfo.getExpires() > 0) {
                ExpiresHeader expiresHeader = SipRequestUtils.createExpiresHeader(subscribeInfo.getExpires());
                sipMessage.addHeader(expiresHeader);
            }

            if (subscribeInfo.getEventType() != null && subscribeInfo.getEventId() != null) {
                EventHeader eventHeader = SipRequestUtils.createEventHeader(subscribeInfo.getEventType(), subscribeInfo.getEventId());
                sipMessage.addHeader(eventHeader);
            }

            if (subscribeInfo.getSubscriptionState() != null) {
                SubscriptionStateHeader subscriptionStateHeader = SipRequestUtils.createSubscriptionStateHeader(subscribeInfo.getSubscriptionState());
                sipMessage.addHeader(subscriptionStateHeader);
            }
        }

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

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
     * 创建Register请求
     *
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param callId callId
     * @return Request
     */
    public static Request createRegisterRequest(FromDevice fromDevice, ToDevice toDevice, String callId, Integer expires) {

        SipMessage sipMessage = SipMessage.getRegisterBody();
        sipMessage.setMethod(Request.REGISTER);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());
        ExpiresHeader expiresHeader = SipRequestUtils.createExpiresHeader(expires);

        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader).addHeader(expiresHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage);
    }

    /**
     * 带签名的注册构造器
     *
     *
     * @param www 认证头
     * @return Request
     */
    public static Request createRegisterRequestWithAuth(FromDevice fromDevice, ToDevice toDevice, String callId, Integer expires,
        WWWAuthenticateHeader www) {

        Request registerRequest = createRegisterRequest(fromDevice, toDevice, callId, expires);
        URI requestURI = registerRequest.getRequestURI();

        String userId = toDevice.getUserId();
        String password = toDevice.getPassword();
        if (www == null) {
            try {
                AuthorizationHeader authorizationHeader = SipRequestUtils.createAuthorizationHeader("Digest");
                String username = fromDevice.getUserId();
                authorizationHeader.setUsername(username);
                authorizationHeader.setURI(requestURI);
                authorizationHeader.setAlgorithm("MD5");
                registerRequest.addHeader(authorizationHeader);
                return registerRequest;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        String realm = www.getRealm();
        String nonce = www.getNonce();
        String scheme = www.getScheme();

        // 参考 https://blog.csdn.net/y673533511/article/details/88388138
        // qop 保护质量 包含auth（默认的）和auth-int（增加了报文完整性检测）两种策略
        String qop = www.getQop();

        String cNonce = null;
        String nc = "00000001";
        if (qop != null) {
            if ("auth".equalsIgnoreCase(qop)) {
                // 客户端随机数，这是一个不透明的字符串值，由客户端提供，并且客户端和服务器都会使用，以避免用明文文本。
                // 这使得双方都可以查验对方的身份，并对消息的完整性提供一些保护
                cNonce = UUID.randomUUID().toString();

            } else if ("auth-int".equalsIgnoreCase(qop)) {
                // TODO
            }
        }
        String HA1 = DigestUtils.md5DigestAsHex((userId + ":" + realm + ":" + password).getBytes());
        String HA2 = DigestUtils.md5DigestAsHex((Request.REGISTER + ":" + requestURI.toString()).getBytes());

        StringBuffer reStr = new StringBuffer(200);
        reStr.append(HA1);
        reStr.append(":");
        reStr.append(nonce);
        reStr.append(":");
        if (qop != null) {
            reStr.append(nc);
            reStr.append(":");
            reStr.append(cNonce);
            reStr.append(":");
            reStr.append(qop);
            reStr.append(":");
        }
        reStr.append(HA2);

        String RESPONSE = DigestUtils.md5DigestAsHex(reStr.toString().getBytes());

        AuthorizationHeader authorizationHeader =
            SipRequestUtils.createAuthorizationHeader(scheme, userId, requestURI, realm, nonce, qop, cNonce, RESPONSE);
        registerRequest.addHeader(authorizationHeader);

        return registerRequest;
    }

    /**
     * 创建Subscribe请求
     *
     * @param fromDevice 发送设备
     * @param toDevice 发送目的设备
     * @param content 内容
     * @param callId callId

     * @return Request
     */
    public static Request createSubscribeRequest(FromDevice fromDevice, ToDevice toDevice, String content, SubscribeInfo subscribeInfo, String callId) {
        SipMessage sipMessage = SipMessage.getSubscribeBody();
        sipMessage.setMethod(Request.SUBSCRIBE);
        sipMessage.setContent(content);
        sipMessage.setCallId(callId);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());

        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage, subscribeInfo);
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

    /**
     * 创建Notify请求
     *
     * @param fromDevice 发送设备
     * @param toDevice   发送目的设备
     * @param callId     callId
     * @return Request
     */
    public static Request createNotifyRequest(FromDevice fromDevice, ToDevice toDevice, String content, SubscribeInfo subscribeInfo, String callId) {
        SipMessage sipMessage = SipMessage.getNotifyBody();
        sipMessage.setMethod(Request.NOTIFY);
        sipMessage.setCallId(callId);
        sipMessage.setContent(content);

        UserAgentHeader userAgentHeader = SipRequestUtils.createUserAgentHeader(fromDevice.getAgent());
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(fromDevice.getUserId(), fromDevice.getHostAddress());
        sipMessage.addHeader(userAgentHeader).addHeader(contactHeader);

        return createSipRequest(fromDevice, toDevice, sipMessage, subscribeInfo);
    }

    public Request createPlaybackInviteRequest(FromDevice fromDevice, ToDevice toDevice, String content, String callId) {
        return createInviteRequest(fromDevice, toDevice, content, callId);
    }
}
