package io.github.lunasaw.sip.common.utils;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;

/**
 * @author luna
 */
public class SipUtils {


    public static String getUserIdFromToHeader(Request request) {
        ToHeader toHeader = (ToHeader)request.getHeader(ToHeader.NAME);
        return getUserIdFromFromHeader(toHeader);
    }

    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
        return getUserIdFromFromHeader(fromHeader);
    }

    public static String getUserIdFromFromHeader(HeaderAddress headerAddress) {
        AddressImpl address = (AddressImpl) headerAddress.getAddress();
        SipUri uri = (SipUri)address.getURI();
        return uri.getUser();
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
}