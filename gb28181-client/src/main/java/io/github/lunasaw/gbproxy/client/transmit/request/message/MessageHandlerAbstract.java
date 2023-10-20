package io.github.lunasaw.gbproxy.client.transmit.request.message;

import java.nio.charset.Charset;

import javax.sip.RequestEvent;
import javax.sip.message.Response;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.transmit.ServerResponseCmd;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import io.github.lunasaw.sip.common.utils.SipUtils;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.Data;

/**
 * @author luna
 */
@Data
@Component
public abstract class MessageHandlerAbstract implements MessageHandler {

    public MessageProcessorClient messageProcessorClient;

    public DeviceSession responseAck(RequestEvent event) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();
        String userId = SipUtils.getUserIdFromToHeader(sipRequest);
        String sipId = SipUtils.getUserIdFromFromHeader(sipRequest);
        String receiveIp = sipRequest.getLocalAddress().getHostAddress();
        ServerResponseCmd.doResponseCmd(Response.OK, "OK", receiveIp, sipRequest);

        return new DeviceSession(userId, sipId);
    }

    public <T> T parseRequest(RequestEvent event, String charset, Class<T> clazz) {
        SIPRequest sipRequest = (SIPRequest) event.getRequest();
        byte[] rawContent = sipRequest.getRawContent();
        if (StringUtils.isBlank(charset)) {
            charset = "gb2312";
        }
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));
        T obj = (T) XmlUtils.parseObj(xmlStr, clazz);
        return obj;
    }
}
