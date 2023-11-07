package io.github.lunasaw.sip.common.transmit;

import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author luna
 * @date 2023/10/19
 */
public class ResponseCmd {

    public static void doResponseCmd(int statusCode, String phrase, String ip, String content, ContentTypeHeader contentTypeHeader, RequestEvent request) {
        SipURI sipURI = (SipURI) request.getRequest().getRequestURI();
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(sipURI.getUser(), sipURI.getHost() + ":" + sipURI.getPort());
        doResponseCmd(statusCode, phrase, content, contentTypeHeader, ip, request, Lists.newArrayList(contactHeader));
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

    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, String ip, RequestEvent event, List<Header> headers) {
        ServerTransaction serverTransaction = event.getServerTransaction();
        if (serverTransaction == null) {
            doResponseCmd(statusCode, phrase, content, contentTypeHeader, ip, event.getRequest(), headers);
        } else {
            try {
                Request request = event.getRequest();
                Response response = SipRequestUtils.createResponse(statusCode, request);
                response.setReasonPhrase(phrase);
                if (StringUtils.isNotBlank(content)) {
                    response.setContent(content, contentTypeHeader);
                }

                SipRequestUtils.setResponseHeader(response, headers);
                serverTransaction.sendResponse(response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, String ip, Request request, List<Header> headers) {
        try {
            Response response = SipRequestUtils.createResponse(statusCode, request);
            response.setReasonPhrase(phrase);
            if (StringUtils.isNotBlank(content)) {
                response.setContent(content, contentTypeHeader);
            }
            SipRequestUtils.setResponseHeader(response, headers);

            ServerTransaction serverTransaction = SipSender.getServerTransaction(request, ip);
            serverTransaction.sendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
