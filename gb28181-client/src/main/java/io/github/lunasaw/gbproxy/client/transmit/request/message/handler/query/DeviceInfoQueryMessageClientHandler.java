package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.transmit.request.message.ClientMessageRequestProcessor;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.gb28181.common.entity.query.DeviceQuery;
import io.github.lunasaw.gb28181.common.entity.response.DeviceInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备信息
 * 
 * @author weidian
 */
@Component
@Slf4j
@Getter
@Setter
public class DeviceInfoQueryMessageClientHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "DeviceInfo";

    private String cmdType = CMD_TYPE;

    public DeviceInfoQueryMessageClientHandler(MessageProcessorClient messageProcessorClient, SipUserGenerateClient sipUserGenerateClient) {
        super(messageProcessorClient, sipUserGenerateClient);
    }


    @Override
    public String getRootType() {
        return ClientMessageRequestProcessor.METHOD + QUERY;
    }


    @Override
    public void handForEvt(RequestEvent event) {

        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();
        String sipId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(sipId);

        DeviceQuery deviceQuery = parseXml(DeviceQuery.class);

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
