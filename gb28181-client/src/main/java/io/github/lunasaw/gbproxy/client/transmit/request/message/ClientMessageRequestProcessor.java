package io.github.lunasaw.gbproxy.client.transmit.request.message;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.request.SipMessageRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

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
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice();

        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }
        // 如果是客户端收到的userId，一定是和自己的userId一致

        doMessageHandForEvt(evt, fromDevice);
    }
}
