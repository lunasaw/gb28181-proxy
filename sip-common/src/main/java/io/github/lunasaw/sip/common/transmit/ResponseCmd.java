package io.github.lunasaw.sip.common.transmit;

import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author luna
 * @date 2023/10/19
 */
public class ResponseCmd {

    public static void doResponseCmd(int statusCode, String phrase, String ip, String conetnt, ContentTypeHeader contentTypeHeader, Request request) {
        doResponseCmd(statusCode, phrase, conetnt, contentTypeHeader, ip, request, new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, String phrase, String ip, Request request) {
        doResponseCmd(statusCode, phrase, ip, request, new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, String phrase, String ip, Request request, Header... headers) {
        doResponseCmd(statusCode, phrase, ip, request, Arrays.asList(headers));
    }

    public static void doResponseCmd(int statusCode, String phrase, String ip, Request request, List<Header> headers) {
        doResponseCmd(statusCode, phrase, null, null, ip, request, headers);
    }


    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, String ip, Request request, List<Header> headers) {
        try {
            Response response = SipRequestUtils.createResponse(statusCode, request);
            response.setReasonPhrase(phrase);
            if (StringUtils.isNotBlank(content)) {
                response.setContent(content, contentTypeHeader);
            }
            SipRequestUtils.setResponseHeader(response, headers);
            SipSender.transmitRequest(ip, response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
