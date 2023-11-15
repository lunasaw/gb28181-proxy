package io.github.lunasaw.sip.common.transmit;

import com.luna.common.check.Assert;
import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.SneakyThrows;
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

    public static void doResponseCmd(int statusCode, RequestEvent request) {
        doResponseCmd(statusCode, null, null, request, new ArrayList<>());
    }

    public static void doResponseCmd(int statusCode, String phrase, RequestEvent request) {
        doResponseCmd(statusCode, phrase, null, request, new ArrayList<>());
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

    public static void doResponseCmd(int statusCode, String phrase, String ip, RequestEvent event, List<Header> headers) {
        doResponseCmd(statusCode, phrase, null, null, ip, event, headers);
    }

    public static void doResponseCmd(int statusCode, String phrase, String content, ContentTypeHeader contentTypeHeader, String ip, RequestEvent event, List<Header> headers) {
        ServerTransaction serverTransaction = getServerTransaction(event, ip);
        doResponseCmd(statusCode, phrase, content, contentTypeHeader, event.getRequest(), serverTransaction, headers);
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

}
