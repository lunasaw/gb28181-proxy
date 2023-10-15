package io.github.lunasaw.sip.common.utils;

import javax.sip.header.FromHeader;
import javax.sip.header.HeaderAddress;
import javax.sip.header.ToHeader;
import javax.sip.message.Request;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;

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

    public static String getUserIdFromFromHeader(HeaderAddress fromHeader) {
        AddressImpl address = (AddressImpl)fromHeader.getAddress();
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
}