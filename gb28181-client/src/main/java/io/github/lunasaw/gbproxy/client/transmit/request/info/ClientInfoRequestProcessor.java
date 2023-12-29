package io.github.lunasaw.gbproxy.client.transmit.request.info;

import javax.sip.RequestEvent;
import javax.sip.message.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luna
 * @date 2023/10/18
 */
@Component
@Getter
@Setter
public class ClientInfoRequestProcessor extends SipRequestProcessorAbstract {

    public static final String    METHOD = "INFO";

    private String                method = METHOD;

    @Autowired
    private InfoProcessorClient   infoProcessorClient;

    @Autowired
    private SipUserGenerateClient sipUserGenerate;

    /**
     * 收到Info请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        SIPRequest request = (SIPRequest)evt.getRequest();
        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        if (!sipUserGenerate.checkDevice(evt)) {
            return;
        }
        try {
            infoProcessorClient.receiveInfo(userId, new String(request.getRawContent()));
            ResponseCmd.doResponseCmd(Response.OK, evt);
        } catch (Exception e) {
            ResponseCmd.doResponseCmd(Response.BAD_GATEWAY, e.getMessage(), evt);
        }
    }

}
