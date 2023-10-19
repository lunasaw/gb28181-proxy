package io.github.lunasaw.gbproxy.client.transmit.request.message.handler;

import java.nio.charset.Charset;

import javax.sip.RequestEvent;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

import com.luna.common.text.StringTools;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.entity.response.DeviceInfo;
import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
import io.github.lunasaw.sip.common.transmit.ServerResponseCmd;
import io.github.lunasaw.sip.common.utils.SipUtils;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备信息
 */
@Component
@Data
@Slf4j
public class DeviceInfoQueryMessageHandler extends MessageHandlerAbstract {

    public static final String CMD_TYPE = "DeviceInfo";

    private String cmdType = CMD_TYPE;

    private MessageProcessorClient messageProcessorClient;

    @Override
    public void handForEvt(RequestEvent evt) {

        SIPRequest sipRequest = (SIPRequest) evt.getRequest();
        String userId = SipUtils.getUserIdFromToHeader(sipRequest);
        String sipId = SipUtils.getUserIdFromFromHeader(sipRequest);
        String receiveIp = sipRequest.getLocalAddress().getHostAddress();
        ServerResponseCmd.doResponseCmd(Response.OK, "OK", receiveIp, sipRequest);

        // 设备查询
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice(userId);
        ToDevice toDevice = (ToDevice) messageProcessorClient.getToDevice(sipId);

        byte[] rawContent = sipRequest.getRawContent();
        String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(fromDevice.getCharset()));
        DeviceQuery deviceQuery = (DeviceQuery) XmlUtils.parseObj(xmlStr, DeviceQuery.class);

        String sn = deviceQuery.getSn();
        DeviceInfo deviceInfo = messageProcessorClient.getDeviceInfo(userId);
        deviceInfo.setSn(sn);

        ClientSendCmd.deviceInfoResponse(fromDevice, toDevice, deviceInfo);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
