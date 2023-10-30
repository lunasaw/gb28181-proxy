package io.github.lunasaw.gbproxy.client.transmit.request.message;

import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import io.github.lunasaw.sip.common.utils.SipUtils;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.Data;

/**
 * @author luna
 */
@Data
@Component
public abstract class MessageClientHandlerAbstract implements MessageHandler {

    @Resource
    public MessageProcessorClient messageProcessorClient;

    public MessageClientHandlerAbstract(MessageProcessorClient messageProcessorClient) {
        this.messageProcessorClient = messageProcessorClient;
    }

    public DeviceSession getDeviceSession(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();

        // 特别注意。这里的userId和sipId是反的，因为是客户端收到消息，所以这里的from是服务端，to是客户端
        String userId = SipUtils.getUserIdFromToHeader(sipRequest);
        String sipId = SipUtils.getUserIdFromFromHeader(sipRequest);

        return new DeviceSession(userId, sipId);
    }

    public void responseAck(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest)event.getRequest();
        String receiveIp = sipRequest.getLocalAddress().getHostAddress();
        ResponseCmd.doResponseCmd(Response.OK, "OK", receiveIp, sipRequest);
    }

    public void responseError(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest)event.getRequest();
        String receiveIp = sipRequest.getLocalAddress().getHostAddress();
        ResponseCmd.doResponseCmd(Response.SERVER_INTERNAL_ERROR, "OK", receiveIp, sipRequest);
    }

    public <T> T parseRequest(RequestEvent event, String charset, Class<T> clazz) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();
        byte[] rawContent = sipRequest.getRawContent();
        if (StringUtils.isBlank(charset)) {
            charset = Constant.GB2312;
        }
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));
        Object o = XmlUtils.parseObj(xmlStr, clazz);
        return (T) o;
    }
}
