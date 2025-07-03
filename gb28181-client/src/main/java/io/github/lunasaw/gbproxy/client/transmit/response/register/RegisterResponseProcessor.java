package io.github.lunasaw.gbproxy.client.transmit.response.register;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sdp.SdpParseException;
import javax.sip.ResponseEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.SipMethod;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * REGISTER响应处理器
 * 处理客户端发起REGISTER后的响应
 * 业务逻辑直接继承该类，实现方法即可
 *
 * @author luna
 */
@SipMethod("REGISTER")
@Slf4j
@Getter
@Setter
@Component
public class RegisterResponseProcessor extends SipResponseProcessorAbstract {

    @Autowired
    private RegisterProcessorClient registerProcessorClient;

    @Autowired
    private SipUserGenerateClient   sipUserGenerate;

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

        ResponseEventExt eventExt = (ResponseEventExt)evt;
        if (response.getStatusCode() == Response.UNAUTHORIZED) {
            try {
                responseUnAuthorized(eventExt);
            } catch (SdpParseException e) {
                log.error("process responseUnAuthorized error::evt = {} ", evt, e);
            }
        } else if (response.getStatusCode() == Response.OK) {
            String toUserId = SipUtils.getUserIdFromToHeader(response);
            registerProcessorClient.registerSuccess(toUserId);
        }
    }

    /**
     * 处理401未授权响应
     *
     * @param event 响应事件
     * @throws SdpParseException SDP解析异常
     */
    public void responseUnAuthorized(ResponseEventExt event) throws SdpParseException {
        SIPResponse response = (SIPResponse)event.getResponse();
        WWWAuthenticateHeader www = (WWWAuthenticateHeader)response.getHeader(WWWAuthenticateHeader.NAME);
        String callId = event.getResponse().getHeader(CallIdHeader.NAME).toString();
        callId = callId.substring(callId.indexOf(":") + 1).trim();
        Request request = event.getOriginalRequest();

        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
        ToDevice toDevice = sipUserGenerate.getToDevice(SipUtils.getUserIdFromToHeader(response));

        Request authRequest = SipRequestProvider.createAuthRequest(fromDevice, toDevice, request, www, callId);
        SipSender.transmitRequest(fromDevice.getIp(), authRequest);
    }
}
