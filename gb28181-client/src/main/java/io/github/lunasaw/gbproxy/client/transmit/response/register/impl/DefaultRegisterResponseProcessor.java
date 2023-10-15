package io.github.lunasaw.gbproxy.client.transmit.response.register.impl;

import javax.sip.SipException;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;

import io.github.lunasaw.sip.common.transmit.SipRequestProvider;
import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterResponseProcessor;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.SipTransaction;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.transmit.SipSender;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author weidian
 * @date 2023/10/14
 */
@Slf4j
@Data
public class DefaultRegisterResponseProcessor extends RegisterResponseProcessor {

    private FromDevice fromDevice;
    private ToDevice   toDevice;
    private Integer    expires;

    public DefaultRegisterResponseProcessor(FromDevice fromDevice, ToDevice toDevice, Integer expires) {
        this.fromDevice = fromDevice;
        this.toDevice = toDevice;
        this.expires = expires;
    }

    @Override
    public void unAuthorized(WWWAuthenticateHeader www, SipTransaction sipTransaction) {
        log.info("unAuthorized::www = {}, sipTransaction = {}", www, sipTransaction);

        // 构造二次请求
        Request registerRequestWithAuth =
            SipRequestProvider.createRegisterRequestWithAuth(fromDevice, toDevice, sipTransaction.getCallId(), expires, www);

        // 发送二次请求
        SipSender.transmitRequest(fromDevice.getIp(), registerRequestWithAuth);
    }

    @Override
    public void success() {
        log.info("success::注册成功");
    }
}