package io.github.lunasaw.gbproxy.client.transmit.request.info;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

/**
 * @author luna
 * @date 2023/10/18
 */
@Component
@Getter
@Setter
public class ClientInfoRequestProcessor extends SipRequestProcessorAbstract {


    public static final String METHOD = "INFO";

    private String method = METHOD;

    @Autowired
    private InfoProcessorClient infoProcessorClient;

    /**
     * 收到Info请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice) infoProcessorClient.getFromDevice();

        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

    }

}
