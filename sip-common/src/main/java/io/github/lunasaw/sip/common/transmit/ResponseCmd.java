package io.github.lunasaw.sip.common.transmit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.utils.SipRequestUtils;

/**
 * @author luna
 * @date 2023/10/19
 */
public class ResponseCmd {

    public static void doResponseCmd(int statusCode, String phrase, String ip, Request request) {
        doResponseCmd(statusCode, phrase, ip, request, new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, String phrase, String ip, Request request, Header... headers) {
        doResponseCmd(statusCode, phrase, ip, request, Arrays.asList(headers));
    }

    public static void doResponseCmd(int statusCode, String phrase, String ip, Request request, List<Header> headers) {
        try {
            Response response = SipRequestUtils.createResponse(statusCode, request);
            response.setReasonPhrase(phrase);
            SipRequestUtils.setResponseHeader(response, headers);
            SipSender.transmitRequest(ip, response);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
