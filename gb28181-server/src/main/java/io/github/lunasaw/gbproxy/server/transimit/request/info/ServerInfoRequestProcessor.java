package io.github.lunasaw.gbproxy.server.transimit.request.info;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.utils.SipUtils;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;

/**
 * SIP命令类型： 收到info请求
 *
 * @author luna
 */
@Component
@Getter
@Setter
public class ServerInfoRequestProcessor extends SipRequestProcessorAbstract {

    public static final String METHOD = "INFO";

    private String method = METHOD;

    @Resource
    private InfoProcessorServer infoProcessorServer;

    /**
     * 收到Info请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest) evt.getRequest();

        // 在服务端看来 收到请求的时候fromHeader还是客户端的 toHeader才是自己的，这里是要查询自己的信息
        String sip = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice) infoProcessorServer.getFromDevice();
        if (!sip.equals(fromDevice.getUserId())) {
            return;
        }

        String userId = SipUtils.getUserIdFromFromHeader(request);

        //TODO 解析请求
    }

}
