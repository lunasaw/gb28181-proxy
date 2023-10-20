package io.github.lunasaw.gbproxy.client.transmit.request.message.handler;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
import io.github.lunasaw.sip.common.entity.response.DeviceInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备信息
 */
@Component
@Slf4j
public class DeviceInfoQueryMessageHandler extends MessageHandlerAbstract {

    public static final String CMD_TYPE = "DeviceInfo";

    private String cmdType = CMD_TYPE;

    private MessageProcessorClient messageProcessorClient;

    public DeviceInfoQueryMessageHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
    }

    @Override
    public void handForEvt(RequestEvent evt) {

        DeviceSession deviceSession = responseAck(evt);
        String userId = deviceSession.getUserId();
        String sipId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice(userId);
        ToDevice toDevice = (ToDevice) messageProcessorClient.getToDevice(sipId);

        DeviceQuery deviceQuery = parseRequest(evt, fromDevice.getCharset(), DeviceQuery.class);

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
