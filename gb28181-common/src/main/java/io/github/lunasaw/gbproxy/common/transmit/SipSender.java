package io.github.lunasaw.gbproxy.common.transmit;

import java.text.ParseException;

import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;

import io.github.lunasaw.gbproxy.common.constant.Constant;
import io.github.lunasaw.gbproxy.common.layer.SipLayer;
import io.github.lunasaw.gbproxy.common.transmit.event.Event;
import io.github.lunasaw.gbproxy.common.transmit.event.SipSubscribe;
import io.github.lunasaw.gbproxy.common.utils.SipRequestUtils;
import io.github.lunasaw.gbproxy.common.utils.SipUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import gov.nist.javax.sip.SipProviderImpl;

/**
 * 发送SIP消息
 * 
 * @author lin
 */
@Component
@Slf4j
@Data
public class SipSender {

    @Value("${sip.agent:gb28181-proxy}")
    private String       agent;

    @Autowired
    private SipLayer     sipLayer;

    @Autowired
    private SipSubscribe sipSubscribe;

    public void transmitRequest(String ip, Message message) throws SipException, ParseException {
        transmitRequest(ip, message, null, null);
    }

    public void transmitRequest(String ip, Message message, Event errorEvent) throws SipException, ParseException {
        transmitRequest(ip, message, errorEvent, null);
    }

    public void transmitRequest(String ip, Message message, Event errorEvent, Event okEvent) throws SipException {
        ViaHeader viaHeader = (ViaHeader)message.getHeader(ViaHeader.NAME);
        String transport = "UDP";
        if (viaHeader == null) {
            log.warn("[消息头缺失]： ViaHeader， 使用默认的UDP方式处理数据");
        } else {
            transport = viaHeader.getTransport();
        }
        if (message.getHeader(UserAgentHeader.NAME) == null) {
            message.addHeader(SipRequestUtils.createUserAgentHeader(agent));
        }

        CallIdHeader callIdHeader = (CallIdHeader)message.getHeader(CallIdHeader.NAME);
        // 添加错误订阅
        if (errorEvent != null) {
            sipSubscribe.addErrorSubscribe(callIdHeader.getCallId(), (eventResult -> {
                errorEvent.response(eventResult);
                sipSubscribe.removeErrorSubscribe(eventResult.callId);
                sipSubscribe.removeOkSubscribe(eventResult.callId);
            }));
        }
        // 添加订阅
        if (okEvent != null) {
            sipSubscribe.addOkSubscribe(callIdHeader.getCallId(), eventResult -> {
                okEvent.response(eventResult);
                sipSubscribe.removeOkSubscribe(eventResult.callId);
                sipSubscribe.removeErrorSubscribe(eventResult.callId);
            });
        }
        if (Constant.TCP.equals(transport)) {
            SipProviderImpl tcpSipProvider = sipLayer.getTcpSipProvider(ip);
            if (tcpSipProvider == null) {
                log.error("[发送信息失败] 未找到tcp://{}的监听信息", ip);
                return;
            }
            if (message instanceof Request) {
                tcpSipProvider.sendRequest((Request)message);
            } else if (message instanceof Response) {
                tcpSipProvider.sendResponse((Response)message);
            }

        } else if (Constant.UDP.equals(transport)) {
            SipProviderImpl sipProvider = sipLayer.getUdpSipProvider(ip);
            if (sipProvider == null) {
                log.error("[发送信息失败] 未找到udp://{}的监听信息", ip);
                return;
            }
            if (message instanceof Request) {
                sipProvider.sendRequest((Request)message);
            } else if (message instanceof Response) {
                sipProvider.sendResponse((Response)message);
            }
        }
    }

    public CallIdHeader getNewCallIdHeader(String ip, String transport) {
        if (ObjectUtils.isEmpty(transport)) {
            return sipLayer.getUdpSipProvider().getNewCallId();
        }
        SipProviderImpl sipProvider;
        if (ObjectUtils.isEmpty(ip)) {
            sipProvider = transport.equalsIgnoreCase(Constant.TCP) ? sipLayer.getTcpSipProvider()
                : sipLayer.getUdpSipProvider();
        } else {
            sipProvider = transport.equalsIgnoreCase(Constant.TCP) ? sipLayer.getTcpSipProvider(ip)
                : sipLayer.getUdpSipProvider(ip);
        }

        if (sipProvider == null) {
            sipProvider = sipLayer.getUdpSipProvider();
        }

        if (sipProvider != null) {
            return sipProvider.getNewCallId();
        } else {
            log.warn("[新建CallIdHeader失败]， ip={}, transport={}", ip, transport);
            return null;
        }
    }
}
