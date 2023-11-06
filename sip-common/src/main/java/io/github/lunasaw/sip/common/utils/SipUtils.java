package io.github.lunasaw.sip.common.utils;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.RequestEvent;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;

import com.luna.common.text.StringTools;
import gov.nist.javax.sip.header.Subject;
import io.github.lunasaw.sip.common.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import io.github.lunasaw.sip.common.entity.SipTransaction;

import java.nio.charset.Charset;

/**
 * @author luna
 */
public class SipUtils {

    public static String getUserIdFromToHeader(Response response) {
        ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(toHeader);
    }


    public static String getUserIdFromFromHeader(Response response) {
        FromHeader fromHeader = (FromHeader) response.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static String getUserIdFromToHeader(Request request) {
        ToHeader toHeader = (ToHeader)request.getHeader(ToHeader.NAME);
        return getUserIdFromHeader(toHeader);
    }

    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
        return getUserIdFromHeader(fromHeader);
    }

    public static SipTransaction getSipTransaction(SIPResponse response) {
        SipTransaction sipTransaction = new SipTransaction();
        sipTransaction.setCallId(response.getCallIdHeader().getCallId());
        sipTransaction.setFromTag(response.getFromTag());
        sipTransaction.setToTag(response.getToTag());
        sipTransaction.setViaBranch(response.getTopmostViaHeader().getBranch());
        return sipTransaction;
    }

    public static SipTransaction getSipTransaction(SIPRequest request) {
        SipTransaction sipTransaction = new SipTransaction();
        sipTransaction.setCallId(request.getCallIdHeader().getCallId());
        sipTransaction.setFromTag(request.getFromTag());
        sipTransaction.setToTag(request.getToTag());
        sipTransaction.setViaBranch(request.getTopmostViaHeader().getBranch());
        return sipTransaction;
    }

    public static String getUserIdFromHeader(HeaderAddress headerAddress) {
        AddressImpl address = (AddressImpl) headerAddress.getAddress();
        SipUri uri = (SipUri)address.getURI();
        return uri.getUser();
    }

    public static String getCallId(SIPRequest request) {
        return request.getCallIdHeader().getCallId();
    }

    public static RemoteAddressInfo getRemoteAddressFromRequest(SIPRequest request) {
        return getRemoteAddressFromRequest(request, false);
    }

    /**
     * 从subject读取channelId
     */
    public static String getSubjectId(Request request) {
        SubjectHeader subject = (SubjectHeader) request.getHeader(SubjectHeader.NAME);
        if (subject == null) {
            // 如果缺失subject
            return null;
        }
        return subject.getSubject().split(":")[0];
    }


    /**
     * 从请求中获取设备ip地址和端口号
     *
     * @param request                       请求
     * @param sipUseSourceIpAsRemoteAddress false 从via中获取地址， true 直接获取远程地址
     * @return 地址信息
     */
    public static RemoteAddressInfo getRemoteAddressFromRequest(SIPRequest request, boolean sipUseSourceIpAsRemoteAddress) {

        String remoteAddress;
        int remotePort;
        if (sipUseSourceIpAsRemoteAddress) {
            remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
            remotePort = request.getPeerPacketSourcePort();

        } else {
            // 判断RPort是否改变，改变则说明路由nat信息变化，修改设备信息
            // 获取到通信地址等信息
            remoteAddress = request.getTopmostViaHeader().getReceived();
            remotePort = request.getTopmostViaHeader().getRPort();
            // 解析本地地址替代
            if (ObjectUtils.isEmpty(remoteAddress) || remotePort == -1) {
                remoteAddress = request.getPeerPacketSourceAddress().getHostAddress();
                remotePort = request.getPeerPacketSourcePort();
            }
        }

        return new RemoteAddressInfo(remoteAddress, remotePort);
    }

    public static String generateGB28181Code(int centerCode, int industryCode, int typeCode, int serialNumber) {
        String centerCodeStr = String.format("%08d", centerCode);
        String industryCodeStr = String.format("%02d", industryCode);
        String typeCodeStr = String.format("%03d", typeCode);
        String serialNumberStr = String.format("%07d", serialNumber);
        return centerCodeStr + industryCodeStr + typeCodeStr + serialNumberStr;
    }

    public static SdpSessionDescription parseSdp(String sdpStr) throws SdpParseException {
        // jainSip 不支持y= f=字段， 移除以解析。
        int ssrcIndex = sdpStr.indexOf("y=");
        int mediaDescriptionIndex = sdpStr.indexOf("f=");
        // 检查是否有y字段
        SessionDescription sdp;
        String ssrc = null;
        String mediaDescription = null;
        if (mediaDescriptionIndex == 0 && ssrcIndex == 0) {
            sdp = SdpFactory.getInstance().createSessionDescription(sdpStr);
        } else {
            String lines[] = sdpStr.split("\\r?\\n");
            StringBuilder sdpBuffer = new StringBuilder();
            for (String line : lines) {
                if (line.trim().startsWith("y=")) {
                    ssrc = line.substring(2);
                } else if (line.trim().startsWith("f=")) {
                    mediaDescription = line.substring(2);
                } else {
                    sdpBuffer.append(line.trim()).append("\r\n");
                }
            }
            sdp = SdpFactory.getInstance().createSessionDescription(sdpBuffer.toString());
        }
        return SdpSessionDescription.getInstance(sdp);
    }

    public static <T> T parseRequest(RequestEvent event, String charset, Class<T> clazz) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();
        byte[] rawContent = sipRequest.getRawContent();
        if (StringUtils.isBlank(charset)) {
            charset = Constant.GB2312;
        }
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));
        Object o = XmlUtils.parseObj(xmlStr, clazz);
        return (T) o;
    }

    public static String parseRequest(RequestEvent event, String charset) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();
        byte[] rawContent = sipRequest.getRawContent();
        if (StringUtils.isBlank(charset)) {
            charset = Constant.GB2312;
        }
        return StringTools.toEncodedString(rawContent, Charset.forName(charset));
    }
}