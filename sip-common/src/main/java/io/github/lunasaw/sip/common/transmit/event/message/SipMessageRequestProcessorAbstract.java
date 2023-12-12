package io.github.lunasaw.sip.common.transmit.event.message;

import com.google.common.collect.Maps;
import com.luna.common.text.StringTools;
import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import javax.sip.RequestEvent;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author luna
 */
@Slf4j
public abstract class SipMessageRequestProcessorAbstract extends SipRequestProcessorAbstract {

    public static final Map<String, Map<String, MessageHandler>> MESSAGE_HANDLER_CMD_MAP = new ConcurrentHashMap<>();

    public static void addHandler(MessageHandler messageHandler) {
        if (messageHandler == null) {
            return;
        }
        if (MESSAGE_HANDLER_CMD_MAP.containsKey(messageHandler.getRootType())) {
            MESSAGE_HANDLER_CMD_MAP.get(messageHandler.getRootType()).put(messageHandler.getCmdType(), messageHandler);
        } else {
            ConcurrentMap<String, MessageHandler> newedConcurrentMap = Maps.newConcurrentMap();
            newedConcurrentMap.put(messageHandler.getCmdType(), messageHandler);
            MESSAGE_HANDLER_CMD_MAP.put(messageHandler.getRootType(), newedConcurrentMap);
        }
    }

    public void doMessageHandForEvt(RequestEvent evt, FromDevice fromDevice) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        String charset = Optional.of(fromDevice).map(Device::getCharset).orElse(Constant.UTF_8);

        // 解析xml
        byte[] rawContent = request.getRawContent();
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));


        String cmdType = XmlUtils.getCmdType(xmlStr);
        String rootType = XmlUtils.getRootType(xmlStr);
        Map<String, MessageHandler> messageHandlerMap = MESSAGE_HANDLER_CMD_MAP.get(rootType);

        if (MapUtils.isEmpty(messageHandlerMap)) {
            return;
        }

        MessageHandler messageHandler = messageHandlerMap.get(cmdType);
        if (messageHandler == null) {
            return;
        }
        try {
            messageHandler.setXmlStr(xmlStr);
            messageHandler.handForEvt(evt);
            messageHandler.responseAck(evt);
        } catch (Exception e) {
            log.error("process::evt = {}, e", evt, e);
            messageHandler.responseError(evt);
        }
    }


}
