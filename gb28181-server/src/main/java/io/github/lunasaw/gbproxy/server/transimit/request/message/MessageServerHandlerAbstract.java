package io.github.lunasaw.gbproxy.server.transimit.request.message;

import java.nio.charset.Charset;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.message.Response;

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
public abstract class MessageServerHandlerAbstract implements MessageHandler {

    @Resource
    public MessageProcessorServer messageProcessorServer;

    public MessageServerHandlerAbstract(MessageProcessorServer messageProcessorServer) {
        this.messageProcessorServer = messageProcessorServer;
    }

    public DeviceSession getDeviceSession(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest)event.getRequest();

        String userId = SipUtils.getUserIdFromFromHeader(sipRequest);
        String sipId = SipUtils.getUserIdFromToHeader(sipRequest);

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
        SIPRequest sipRequest = (SIPRequest)event.getRequest();
        byte[] rawContent = sipRequest.getRawContent();
        if (StringUtils.isBlank(charset)) {
            charset = "gb2312";
        }
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));
        T obj = (T)XmlUtils.parseObj(xmlStr, clazz);
        return obj;
    }
}
