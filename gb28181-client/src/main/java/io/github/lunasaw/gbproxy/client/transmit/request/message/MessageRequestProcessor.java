package io.github.lunasaw.gbproxy.client.transmit.request.message;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
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
public class MessageRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "MESSAGE";
    public static final Map<String, MessageHandler> MESSAGE_HANDLER_MAP = new ConcurrentHashMap<>();
    private MessageProcessorClient messageProcessorClient;
    private String method = METHOD;

    public static void addHandler(MessageHandler messageHandler) {
        MESSAGE_HANDLER_MAP.put(messageHandler.getCmdType(), messageHandler);
    }

    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice(userId);
        // 解析xml
        byte[] rawContent = request.getRawContent();
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(fromDevice.getCharset()));
        DeviceBase deviceBase = (DeviceBase) XmlUtils.parseObj(xmlStr, DeviceQuery.class);
        String cmdType = deviceBase.getCmdType();

        MessageHandler messageHandler = MESSAGE_HANDLER_MAP.get(cmdType);

        if (messageHandler == null) {
            log.info("no process::evt = {}, xmlStr = {}", evt.getRequest(), xmlStr);

            return;
        }

        messageHandler.handForEvt(evt);
    }

}
