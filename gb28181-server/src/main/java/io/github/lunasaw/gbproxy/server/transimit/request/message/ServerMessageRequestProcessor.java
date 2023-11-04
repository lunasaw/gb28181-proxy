package io.github.lunasaw.gbproxy.server.transimit.request.message;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.transmit.event.request.SipMessageRequestProcessorAbstract;
import org.springframework.stereotype.Component;

import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 */
@Component
@Getter
@Setter
@Slf4j
public class ServerMessageRequestProcessor extends SipMessageRequestProcessorAbstract {

    public static final String                      METHOD              = "MESSAGE";
    public static final Map<String, MessageHandler> MESSAGE_HANDLER_MAP = new ConcurrentHashMap<>();

    @Resource
    private MessageProcessorServer messageProcessorServer;
    private String                                  method              = METHOD;

    public static void addHandler(MessageHandler messageHandler) {
        MESSAGE_HANDLER_MAP.put(messageHandler.getCmdType(), messageHandler);
    }

    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest)evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice) messageProcessorServer.getFromDevice(userId);

        doMessageHandForEvt(evt, fromDevice);
    }

}
