package io.github.lunasaw.sipproxy.common.utils;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import javax.sip.InvalidArgumentException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.*;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.apache.commons.lang3.RandomStringUtils;

import com.google.common.collect.Lists;
import com.luna.common.text.RandomStrUtil;

import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/13
 */
public class SipRequestUtils {

    private static final MessageFactory MESSAGE_FACTORY;

    private static final HeaderFactory  HEADER_FACTORY;

    private static final AddressFactory ADDRESS_FACTORY;

    static {
        try {
            MESSAGE_FACTORY = SipFactory.getInstance().createMessageFactory();
            HEADER_FACTORY = SipFactory.getInstance().createHeaderFactory();
            ADDRESS_FACTORY = SipFactory.getInstance().createAddressFactory();
        } catch (PeerUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param requestUri requestURI – 此消息的请求 URI 值的新 URI 对象。
     * @param method method – 此消息的方法值的新字符串。
     * @param callId callId – 此消息的 callId 值的新 CallIdHeader 对象。
     * @param cSeq cSeq – 此消息的 cSeq 值的新 CSeqHeader 对象。
     * @param from from – 此消息的 from 值的新 FromHeader 对象。
     * @param to to – 此消息的 to 值的新 ToHeader 对象。
     * @param via via – 此消息的 ViaHeader 的新列表对象。
     * @param maxForwards contentType – 此消息的内容类型值的新内容类型标头对象。
     * @param contentType 响应类型 – 此消息的正文内容值的新对象。
     * @param content 内容
     */
    public static Request createRequest(URI requestUri, String method, CallIdHeader callId, CSeqHeader cSeq, FromHeader from, ToHeader to,
        List<ViaHeader> via, MaxForwardsHeader maxForwards, ContentTypeHeader contentType, Object content) {
        try {
            if (contentType == null){
                return MESSAGE_FACTORY.createRequest(requestUri, method, callId, cSeq, from, to, via, maxForwards);
            }
            return MESSAGE_FACTORY.createRequest(requestUri, method, callId, cSeq, from, to, via, maxForwards, contentType, content);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setRequestHeader(Request request, List<Header> headers) {
        for (Header header : headers) {
            request.addHeader(header);
        }
    }

    /**
     *
     * host – 主机的新字符串值。
     * port – 端口的新整数值。
     * transport – tcp / udp。
     * branch – 代理服务器的新字符串值。
     *
     * @return ViaHeader
     */
    public static ViaHeader createViaHeader(String ip, int port, String transport, String branch) {
        try {
            return HEADER_FACTORY.createViaHeader(ip, port, transport, branch);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ViaHeader> createViaHeader(ViaHeader... viaHeaders) {
        return Lists.newArrayList(viaHeaders);
    }

    /**
     *
     * 70 maxForwards – 最大转发的新整数值。
     */
    public static MaxForwardsHeader createMaxForwardsHeader() {
        try {
            return HEADER_FACTORY.createMaxForwardsHeader(70);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param maxForwards maxForwards – 最大转发的新整数值。
     */
    public static MaxForwardsHeader createMaxForwardsHeader(int maxForwards) {
        try {
            return HEADER_FACTORY.createMaxForwardsHeader(maxForwards);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static CallIdHeader createCallIdHeader(String callId) {
        try {
            return HEADER_FACTORY.createCallIdHeader(callId);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param user sip用户
     * @param host 主机地址 ip:port
     * @return SipURI
     */
    public static SipURI createSipUri(String user, String host) {
        try {
            return ADDRESS_FACTORY.createSipURI(user, host);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Address createAddress(String user, String host) {
        SipURI sipUri = createSipUri(user, host);
        return createAddress(sipUri);
    }

    public static Address createAddress(SipURI sipUri) {
        return ADDRESS_FACTORY.createAddress(sipUri);
    }

    /**
     *
     * @param user sip用户
     * @param host 主机地址 ip:port
     * @param tag 标签
     * @return FromHeader
     */
    public static FromHeader createFromHeader(String user, String host, String tag) {
        Address address = createAddress(user, host);
        return createFromHeader(address, tag);
    }

    /**
     *
     * @param user sip用户
     * @param host 主机地址 ip:port
     * @param tag 标签
     * @return FromHeader
     */
    public static ToHeader createToHeader(String user, String host, String tag) {
        Address address = createAddress(user, host);
        return createToHeader(address, tag);
    }

    /**
     * 根据新提供的地址和标记值创建新的 FromHeader。
     *
     * @param address – 地址的新地址对象。
     * @param tag – 标签的新字符串值。
     * @return FromHeader
     */
    public static FromHeader createFromHeader(Address address, String tag) {
        try {
            return HEADER_FACTORY.createFromHeader(address, tag);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据新提供的地址和标记值创建新的 ToHeader。
     *
     * @param address – 地址的新地址对象。
     * @param tag – 标签的新字符串值，此值可能为空。
     * @return ToHeader
     */
    public static ToHeader createToHeader(Address address, String tag) {
        try {
            return HEADER_FACTORY.createToHeader(address, tag);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于新提供的序列号和方法值创建新的 CSeqHeader。
     *
     * @param sequenceNumber – 序列号的新长整型值。
     * @param method – 方法的新字符串值。
     */
    public static CSeqHeader createCSeqHeader(long sequenceNumber, String method) {
        try {
            return HEADER_FACTORY.createCSeqHeader(sequenceNumber, method);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于新提供的内容类型和内容子类型值创建新的内容类型标头。
     *
     * @param contentType contentType – 新的字符串内容类型值。
     * @param contentSubType contentSubType – 新的字符串内容子类型值。
     * @return ContentTypeHeader
     */
    public static ContentTypeHeader createContentTypeHeader(String contentType, String contentSubType) {
        try {
            return HEADER_FACTORY.createContentTypeHeader(contentType, contentSubType);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserAgentHeader createUserAgentHeader() {
        try {
            return createUserAgentHeader("gbproxy");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static UserAgentHeader createUserAgentHeader(String... agent) {
        List<String> agents = Lists.newArrayList(agent);
        try {
            return HEADER_FACTORY.createUserAgentHeader(agents);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getNewFromTag() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getNewViaTag() {
        return "lunaProxy" + RandomStringUtils.randomNumeric(10);
    }

    /**
     * 联系人标头
     * 
     * @param user 用户设备编号
     * @param host 主机地址 ip:port
     * @return ContactHeader
     */
    public static ContactHeader createContactHeader(String user, String host) {
        Address address = createAddress(user, host);
        return HEADER_FACTORY.createContactHeader(address);
    }

    public static SubjectHeader createSubjectHeader(String subject) {
        try {
            return HEADER_FACTORY.createSubjectHeader(subject);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static ExpiresHeader createExpiresHeader(int expires) {
        try {
            return HEADER_FACTORY.createExpiresHeader(expires);
        } catch (InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    public static EventHeader createEventHeader(String eventType, String eventId) {
        try {
            EventHeader eventHeader = HEADER_FACTORY.createEventHeader(eventType);
            eventHeader.setEventId(eventId);
            return eventHeader;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static EventHeader createEventHeader(String eventType) {
        try {
            EventHeader eventHeader = HEADER_FACTORY.createEventHeader(eventType);
            eventHeader.setEventId(RandomStrUtil.getValidationCode());
            return eventHeader;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 基于新提供的方案值创建新的授权标头。
     * 
     * @param scheme 方案的新字符串值。
     * @return AuthorizationHeader
     */
    public static AuthorizationHeader createAuthorizationHeader(String scheme) {
        try {
            return HEADER_FACTORY.createAuthorizationHeader(scheme);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuthorizationHeader createAuthorizationHeader(String scheme, String user, URI requestUri, String realm, String nonce, String qop,
        String cNonce, String response) {

        AuthorizationHeader authorizationHeader = createAuthorizationHeader(scheme);

        try {
            authorizationHeader.setUsername(user);
            authorizationHeader.setRealm(realm);
            authorizationHeader.setNonce(nonce);
            authorizationHeader.setURI(requestUri);
            authorizationHeader.setResponse(response);
            authorizationHeader.setAlgorithm("MD5");
            if (qop != null) {
                authorizationHeader.setQop(qop);
                authorizationHeader.setCNonce(cNonce);
                authorizationHeader.setNonceCount(1);
            }
            return authorizationHeader;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Header createHeader(String name, String value) {
        try {
            return HEADER_FACTORY.createHeader(name, value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void setContent(Request request, ContentTypeHeader contentType, Object content) {
        request.setContent(content, contentType);
    }
}
