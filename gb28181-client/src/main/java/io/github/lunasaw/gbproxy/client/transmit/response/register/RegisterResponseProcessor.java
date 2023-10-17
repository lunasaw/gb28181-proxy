package io.github.lunasaw.gbproxy.client.transmit.response.register;

import javax.sdp.SdpParseException;
import javax.sip.ResponseEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.lang3.StringUtils;

import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;

/**
 * description 发起后 Register 的响应处理器
 * 业务逻辑直接继承该类，实现方法即可
 * 
 * @author luna
 */
@Slf4j
@Getter
@Setter
public class RegisterResponseProcessor extends SipResponseProcessorAbstract {

    public static final String METHOD = "REGISTER";

    public String method = METHOD;

    private RegisterProcessorUser registerProcessorUser;

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

        ResponseEventExt eventExt = (ResponseEventExt) evt;
        if (response.getStatusCode() == Response.UNAUTHORIZED) {
            try {
                responseUnAuthorized(eventExt);
            } catch (SdpParseException e) {
                log.error("process responseUnAuthorized error::evt = {} ", evt, e);
            }
        } else if (response.getStatusCode() == Response.OK) {
            responseOk(eventExt);
        }
    }

    public void responseUnAuthorized(ResponseEventExt evt) throws SdpParseException {
        // 成功响应
        SIPResponse response = (SIPResponse) evt.getResponse();

        String toUserId = SipUtils.getUserIdFromFromHeader(response.getToHeader());
        String fromUserId = SipUtils.getUserIdFromFromHeader(response.getFromHeader());
        CallIdHeader callIdHeader = response.getCallIdHeader();
        Integer expire = registerProcessorUser.getExpire(toUserId);
        FromDevice fromDevice = registerProcessorUser.getFromDevice(fromUserId);
        ToDevice toDevice = registerProcessorUser.getToDevice(toUserId);


        WWWAuthenticateHeader www = (WWWAuthenticateHeader) response.getHeader(WWWAuthenticateHeader.NAME);
        Request registerRequestWithAuth =
                SipRequestProvider.createRegisterRequestWithAuth(fromDevice, toDevice, callIdHeader.getCallId(), expire, www);

        // 发送二次请求
        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequestWithAuth, this::success);
    }

    public void responseOk(ResponseEventExt evt) {

    }

    public void success(EventResult eventResult) {
        log.info("success::注册成功");
    }
}
