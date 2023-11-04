package io.github.lunasaw.sip.common.transmit.event.request;

import com.luna.common.text.StringTools;
import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import javax.sip.RequestEvent;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luna
 */
@Slf4j
public abstract class SipMessageRequestProcessorAbstract extends SipRequestProcessorAbstract {

    public static final Map<String, List<MessageHandler>> MESSAGE_HANDLER_MAP = new ConcurrentHashMap<>();

    public static void addHandler(MessageHandler messageHandler) {
        if (messageHandler == null) {
            return;
        }
        if (MESSAGE_HANDLER_MAP.containsKey(messageHandler.getRootType())) {
            MESSAGE_HANDLER_MAP.get(messageHandler.getRootType()).add(messageHandler);
        } else {
            MESSAGE_HANDLER_MAP.put(messageHandler.getRootType(), Lists.newArrayList(messageHandler));
        }
    }

    public void doMessageHandForEvt(RequestEvent evt, FromDevice fromDevice) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        String charset = Optional.of(fromDevice).map(Device::getCharset).orElse(Constant.GB2312);

        // 解析xml
        byte[] rawContent = request.getRawContent();
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));


        String cmdType = XmlUtils.getCmdType(xmlStr);
        String rootType = XmlUtils.getRootType(xmlStr);
        List<MessageHandler> messageHandlers = MESSAGE_HANDLER_MAP.get(rootType);

        for (MessageHandler messageHandler : messageHandlers) {

            if (messageHandler == null) {
                return;
            }
            messageHandler.setXmlStr(xmlStr);
            messageHandler.responseAck(evt);
            try {
                if (messageHandler.getCmdType().equals(cmdType)) {
                    messageHandler.handForEvt(evt);
                }
            } catch (Exception e) {
                log.error("process::evt = {} ", evt, e);
                messageHandler.responseError(evt);
            }
        }
    }


}
