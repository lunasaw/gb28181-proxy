package io.github.lunasaw.sip.common.transmit;

import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Message;
import javax.sip.message.Request;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import org.springframework.util.ObjectUtils;

import com.luna.common.text.RandomStrUtil;

import gov.nist.javax.sip.SipProviderImpl;
import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 发送SIP消息
 * 
 * @author lin
 */
@Slf4j
@Data
public class SipSender {

    public static String doSubscribeRequest(FromDevice fromDevice, ToDevice toDevice, XmlBean xmlBean, Integer expires, String event) {
        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SipRequestProvider.createSubscribeRequest(fromDevice, toDevice, xmlBean.toString(), callId, expires, event);
        SipSender.transmitRequest(fromDevice.getIp(), messageRequest);
        return callId;
    }

    public static String doMessageRequest(FromDevice fromDevice, ToDevice toDevice, XmlBean xmlBean) {
        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SipRequestProvider.createMessageRequest(fromDevice, toDevice, xmlBean.toString(), callId);
        SipSender.transmitRequest(fromDevice.getIp(), messageRequest);
        return callId;
    }

    public static String doNotifyRequest(FromDevice fromDevice, ToDevice toDevice, XmlBean xmlBean, SubscribeInfo subscribeInfo) {
        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SipRequestProvider.createNotifyRequest(fromDevice, toDevice, xmlBean.toString(), subscribeInfo, callId);
        SipSender.transmitRequest(fromDevice.getIp(), messageRequest);
        return callId;
    }

    public static String doMessageRequest(FromDevice fromDevice, ToDevice toDevice, XmlBean xmlBean, Event errorEvent, Event okEvent) {
        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SipRequestProvider.createMessageRequest(fromDevice, toDevice, xmlBean.toString(), callId);
        SipSender.transmitRequest(fromDevice.getIp(), messageRequest, errorEvent, okEvent);
        return callId;
    }

    public static String doByeRequest(FromDevice fromDevice, ToDevice toDevice) {
        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SipRequestProvider.createByeRequest(fromDevice, toDevice, callId);
        SipSender.transmitRequest(fromDevice.getIp(), messageRequest);
        return callId;
    }

    public static void transmitRequest(String ip, Message message) {
        transmitRequest(ip, message, null, null);
    }

    public static void transmitRequest(String ip, Message message, Event errorEvent) {
        transmitRequest(ip, message, errorEvent, null);
    }

    public static void transmitRequestSuccess(String ip, Message message, Event okEvent) {
        transmitRequest(ip, message, null, okEvent);
    }

    public static void transmitRequest(String ip, Message message, Event errorEvent, Event okEvent) {
        ViaHeader viaHeader = (ViaHeader)message.getHeader(ViaHeader.NAME);
        String transport = "UDP";
        if (viaHeader == null) {
            log.warn("[消息头缺失]： ViaHeader， 使用默认的UDP方式处理数据");
        } else {
            transport = viaHeader.getTransport();
        }
        if (message.getHeader(UserAgentHeader.NAME) == null) {
            message.addHeader(SipRequestUtils.createUserAgentHeader(Constant.AGENT));
        }

        CallIdHeader callIdHeader = (CallIdHeader)message.getHeader(CallIdHeader.NAME);
        // 添加错误订阅
        if (errorEvent != null) {
            SipSubscribe.addErrorSubscribe(callIdHeader.getCallId(), (eventResult -> {
                errorEvent.response(eventResult);
                SipSubscribe.removeErrorSubscribe(eventResult.getCallId());
                SipSubscribe.removeOkSubscribe(eventResult.getCallId());
            }));
        }
        // 添加订阅
        if (okEvent != null) {
            SipSubscribe.addOkSubscribe(callIdHeader.getCallId(), eventResult -> {
                okEvent.response(eventResult);
                SipSubscribe.removeOkSubscribe(eventResult.getCallId());
                SipSubscribe.removeErrorSubscribe(eventResult.getCallId());
            });
        }
        try {
            if (Constant.TCP.equals(transport)) {
                SipProviderImpl tcpSipProvider = SipLayer.getTcpSipProvider(ip);
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
                SipProviderImpl sipProvider = SipLayer.getUdpSipProvider(ip);
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
        } catch (SipException e) {
            throw new RuntimeException(e);
        }
    }

    public CallIdHeader getNewCallIdHeader(String ip, String transport) {
        if (ObjectUtils.isEmpty(transport)) {
            return SipLayer.getUdpSipProvider().getNewCallId();
        }
        SipProviderImpl sipProvider;
        if (ObjectUtils.isEmpty(ip)) {
            sipProvider = transport.equalsIgnoreCase(Constant.TCP) ? SipLayer.getTcpSipProvider()
                : SipLayer.getUdpSipProvider();
        } else {
            sipProvider = transport.equalsIgnoreCase(Constant.TCP) ? SipLayer.getTcpSipProvider(ip)
                : SipLayer.getUdpSipProvider(ip);
        }

        if (sipProvider == null) {
            sipProvider = SipLayer.getUdpSipProvider();
        }

        return sipProvider.getNewCallId();
    }
}
