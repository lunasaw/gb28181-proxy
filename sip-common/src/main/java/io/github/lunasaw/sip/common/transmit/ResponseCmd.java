package io.github.lunasaw.sip.common.transmit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.address.SipURI;
import javax.sip.header.ContactHeader;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.Header;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;

import com.luna.common.check.Assert;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/19
 */
public class ResponseCmd {

    public static void doResponseCmdNoTransaction(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader,
        RequestEvent request) {
        SipURI sipURI = (SipURI) request.getRequest().getRequestURI();
        ContactHeader contactHeader = SipRequestUtils.createContactHeader(sipURI.getUser(), sipURI.getHost() + ":" + sipURI.getPort());
        doResponseCmdNoTransaction(statusCode, phrase, content, contentTypeHeader, request.getRequest(), Lists.newArrayList(contactHeader));
    }

    public static void doResponseCmdNoTransaction(int statusCode, RequestEvent request) {
        doResponseCmdNoTransaction(statusCode, null, null, null, request.getRequest(), new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, RequestEvent request) {
        doResponseCmd(statusCode, null, request, new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, String phrase, RequestEvent request) {
        doResponseCmd(statusCode, phrase, request, new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, String phrase, RequestEvent request, Header... headers) {
        doResponseCmd(statusCode, phrase, request, Arrays.asList(headers));
    }

    public static void doResponseCmd(int statusCode, String phrase, RequestEvent event, List<Header> headers) {
        doResponseCmd(statusCode, phrase, null, null, event, headers);
    }


    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, RequestEvent event,
        List<Header> headers) {
        SIPRequest sipRequest = (SIPRequest)event.getRequest();
        ServerTransaction serverTransaction = getServerTransaction(event, null);
        doResponseCmd(statusCode, phrase, content, contentTypeHeader, sipRequest, serverTransaction, headers);
    }


    private static ServerTransaction getServerTransaction(RequestEvent event, String ip) {
        SIPRequest request = (SIPRequest) event.getRequest();
        ServerTransaction serverTransaction = event.getServerTransaction();
        if (StringUtils.isBlank(ip)) {
            ip = request.getLocalAddress().getHostAddress();
        }
        if (serverTransaction == null) {
            serverTransaction = SipSender.getServerTransaction(request, ip);
        }
        return serverTransaction;
    }

    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, String ip, Request request, List<Header> headers) {
        try {
            Response response = getResponse(statusCode, phrase, content, contentTypeHeader, request, headers);
            ServerTransaction serverTransaction = SipSender.getServerTransaction(request, ip);
            serverTransaction.sendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void doResponseCmdNoTransaction(int statusCode, String phrase, Request request, List<Header> headers) {
        doResponseCmdNoTransaction(statusCode, phrase, null, null, request, headers);
    }

    public static void doResponseCmdNoTransaction(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, Request request,
        List<Header> headers) {
        try {
            Response response = getResponse(statusCode, phrase, content, contentTypeHeader, request, headers);
            SIPRequest sipRequest = (SIPRequest)request;
            SipSender.transmitRequest(sipRequest.getLocalAddress().getHostAddress(), response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, Request request, ServerTransaction serverTransaction, List<Header> headers) {
        try {
            Assert.notNull(serverTransaction, "serverTransaction is null");
            Response response = getResponse(statusCode, phrase, content, contentTypeHeader, request, headers);
            serverTransaction.sendResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ServerTransaction getServerTransaction(RequestEvent event) {
        return getServerTransaction(event, null);
    }

    @SneakyThrows
    private static Response getResponse(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, Request request, List<Header> headers) {
        Response response = SipRequestUtils.createResponse(statusCode, request);
        if (StringUtils.isNotBlank(phrase)) {
            response.setReasonPhrase(phrase);
        }
        if (StringUtils.isNotBlank(content)) {
            response.setContent(content, contentTypeHeader);
        }
        SipRequestUtils.setResponseHeader(response, headers);
        return response;
    }

    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, RequestEvent evt) {
        doResponseCmd(statusCode, phrase, content, contentTypeHeader, evt, null);
    }
}
