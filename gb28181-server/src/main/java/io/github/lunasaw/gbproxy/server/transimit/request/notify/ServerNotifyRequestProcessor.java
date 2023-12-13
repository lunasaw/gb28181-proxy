package io.github.lunasaw.gbproxy.server.transimit.request.notify;

import java.nio.charset.Charset;
import java.util.Optional;

import javax.annotation.Resource;
import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.event.message.SipMessageRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * SIP命令类型： 收到Notify请求
 *
 * @author luna
 */
@Component
@Getter
@Setter
public class ServerNotifyRequestProcessor extends SipMessageRequestProcessorAbstract {

    public static final String METHOD = "NOTIFY";

    private String             method = METHOD;

    @Resource
    public NotifyProcessorServer notifyProcessorServer;

    /**
     * 收到Notify请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest)evt.getRequest();

        // 在服务端看来 收到请求的时候fromHeader还是客户端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice)notifyProcessorServer.getFromDevice();
        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

        doMessageHandForEvt(evt, fromDevice);
    }

}
