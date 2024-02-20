package io.github.lunasaw.gbproxy.server.transimit.request.info;

import org.springframework.beans.factory.annotation.Autowired;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
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

    public static final String    METHOD = "INFO";

    private String                method = METHOD;

    @Autowired
    private InfoProcessorServer   infoProcessorServer;

    @Autowired
    private SipUserGenerateServer sipUserGenerate;

    /**
     * 收到Info请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest)evt.getRequest();

        // 在服务端看来 收到请求的时候fromHeader还是客户端的 toHeader才是自己的，这里是要查询自己的信息
        String sip = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
        if (!sip.equals(fromDevice.getUserId())) {
            return;
        }

        String userId = SipUtils.getUserIdFromFromHeader(request);

        infoProcessorServer.dealInfo(userId, new String(evt.getRequest().getRawContent()));
    }

}
