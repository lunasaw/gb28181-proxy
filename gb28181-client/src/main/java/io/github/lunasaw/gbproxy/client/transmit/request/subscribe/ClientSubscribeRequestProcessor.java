package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.transmit.event.subscribe.SubscribeHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * SIP命令类型： 收到Subscribe请求
 *
 * @author luna
 */
@Component
@Getter
@Setter
@Slf4j
public class ClientSubscribeRequestProcessor extends SipRequestProcessorAbstract {

    public static final String       METHOD = "SUBSCRIBE";

    private String                   method = METHOD;

    @Autowired
    private SubscribeProcessorClient subscribeProcessorClient;

    /**
     * 收到SUBSCRIBE请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

        SIPRequest request = (SIPRequest)evt.getRequest();

        // 在服务端看来 收到请求的时候fromHeader还是客户端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice)subscribeProcessorClient.getFromDevice();
        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

        doSubscribeHandForEvt(evt, fromDevice);
    }

    public static final Map<String, Map<String, SubscribeHandler>> SUBSCRIBE_HANDLER_CMD_MAP = new ConcurrentHashMap<>();

    public static void addHandler(SubscribeHandler SubscribeHandler) {
        if (SubscribeHandler == null) {
            return;
        }

        if (SUBSCRIBE_HANDLER_CMD_MAP.containsKey(SubscribeHandler.getRootType())) {
            SUBSCRIBE_HANDLER_CMD_MAP.get(SubscribeHandler.getRootType()).put(SubscribeHandler.getCmdType(), SubscribeHandler);
        } else {
            ConcurrentMap<String, SubscribeHandler> newedConcurrentMap = Maps.newConcurrentMap();
            newedConcurrentMap.put(SubscribeHandler.getCmdType(), SubscribeHandler);
            SUBSCRIBE_HANDLER_CMD_MAP.put(SubscribeHandler.getRootType(), newedConcurrentMap);
        }
    }

    public void doSubscribeHandForEvt(RequestEvent evt, FromDevice fromDevice) {
        SIPRequest request = (SIPRequest)evt.getRequest();

        String charset = Optional.of(fromDevice).map(Device::getCharset).orElse(Constant.UTF_8);

        // 解析xml
        byte[] rawContent = request.getRawContent();
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));

        String cmdType = XmlUtils.getCmdType(xmlStr);
        String rootType = XmlUtils.getRootType(xmlStr);

        Map<String, SubscribeHandler> SubscribeHandlerMap = SUBSCRIBE_HANDLER_CMD_MAP.get(rootType);

        if (MapUtils.isEmpty(SubscribeHandlerMap)) {
            return;
        }

        SubscribeHandler SubscribeHandler = SubscribeHandlerMap.get(cmdType);
        if (SubscribeHandler == null) {
            return;
        }
        try {
            SubscribeHandler.setXmlStr(xmlStr);
            SubscribeHandler.handForEvt(evt);
            SubscribeHandler.responseAck(evt);
        } catch (Exception e) {
            log.error("process::evt = {}, e = {}", evt, e.getMessage());
            SubscribeHandler.responseError(evt);
        }
    }

}
