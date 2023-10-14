package io.github.lunasaw.gbproxy.client.transmit.response.processor;

import javax.sip.ResponseEvent;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Response;

import org.apache.commons.lang3.StringUtils;

import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.gbproxy.client.transmit.response.SipResponseProcessorAbstract;
import io.github.lunasaw.sipproxy.common.entity.SipTransaction;
import lombok.extern.slf4j.Slf4j;

/**
 * description Register 响应处理器
 * 
 * @author luna
 */
@Slf4j
public abstract class RegisterResponseProcessor extends SipResponseProcessorAbstract {

    public static final String METHOD = "REGISTER";

    /**
     * 处理Register响应
     *
     * @param evt 事件
     */
    @Override
    public void process(ResponseEvent evt) {
        SIPResponse response = (SIPResponse)evt.getResponse();
        String callId = response.getCallIdHeader().getCallId();
        if (StringUtils.isBlank(callId)) {
            return;
        }

        if (response.getStatusCode() == Response.UNAUTHORIZED) {
            WWWAuthenticateHeader www = (WWWAuthenticateHeader)response.getHeader(WWWAuthenticateHeader.NAME);
            SipTransaction sipTransaction = new SipTransaction(response);

            unAuthorized(www, sipTransaction);
        } else if (response.getStatusCode() == Response.OK) {
            success();
        }
    }

    public void unAuthorized(WWWAuthenticateHeader www, SipTransaction sipTransaction) {
        // 二次验证
    }

    public void success() {
        // 注册完成
    }
}
