package io.github.lunasaw.gbproxy.client.transmit.response.register;

import javax.sip.ResponseEvent;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.SipTransaction;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * description 发起后 Register 的响应处理器
 * 业务逻辑直接继承该类，实现方法即可
 * @author luna
 */
@Slf4j
@Component
@Getter
@Setter
@NoArgsConstructor
public class RegisterResponseProcessor extends SipResponseProcessorAbstract {

    public static final String METHOD = "REGISTER";

    public String method = METHOD;

    private FromDevice fromDevice;
    private ToDevice toDevice;
    private Integer expires;

    public RegisterResponseProcessor(FromDevice fromDevice, ToDevice toDevice, Integer expires) {
        this.fromDevice = fromDevice;
        this.toDevice = toDevice;
        this.expires = expires;
    }

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
        log.info("unAuthorized::www = {}, sipTransaction = {}", www, sipTransaction);

        // 构造二次请求
        Request registerRequestWithAuth =
                SipRequestProvider.createRegisterRequestWithAuth(fromDevice, toDevice, sipTransaction.getCallId(), expires, www);

        // 发送二次请求
        SipSender.transmitRequest(fromDevice.getIp(), registerRequestWithAuth);
    }

    public void success() {
        log.info("success::注册成功");
    }
}
