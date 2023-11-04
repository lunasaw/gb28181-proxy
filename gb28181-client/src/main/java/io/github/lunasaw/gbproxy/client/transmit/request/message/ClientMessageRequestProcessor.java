package io.github.lunasaw.gbproxy.client.transmit.request.message;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.transmit.event.request.SipMessageRequestProcessorAbstract;
import org.assertj.core.util.Lists;
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
public class ClientMessageRequestProcessor extends SipMessageRequestProcessorAbstract {

    public static final String METHOD = "MESSAGE";

    @Resource
    private MessageProcessorClient messageProcessorClient;
    private String method = METHOD;



    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice(userId);
        doMessageHandForEvt(evt, fromDevice);
    }



}
