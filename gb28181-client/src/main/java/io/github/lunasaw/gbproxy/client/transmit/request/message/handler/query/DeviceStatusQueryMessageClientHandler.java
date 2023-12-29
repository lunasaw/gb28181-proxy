package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.gb28181.common.entity.query.DeviceQuery;
import io.github.lunasaw.gb28181.common.entity.response.DeviceStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备状态查询
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class DeviceStatusQueryMessageClientHandler extends MessageClientHandlerAbstract {

    public static final String     CMD_TYPE = "DeviceStatus";

    private String root = QUERY;
    private String                 cmdType  = CMD_TYPE;

    public DeviceStatusQueryMessageClientHandler(MessageProcessorClient messageProcessorClient, SipUserGenerateClient sipUserGenerateClient) {
        super(messageProcessorClient, sipUserGenerateClient);
    }


    @Override
    public String getRootType() {
        return QUERY;
    }


    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();
        String sipId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
        if (fromDevice == null) {
            return;
        }
        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(sipId);

        DeviceQuery deviceQuery = parseXml(DeviceQuery.class);

        String sn = deviceQuery.getSn();

        DeviceStatus deviceStatus = messageProcessorClient.getDeviceStatus(userId);
        deviceStatus.setSn(sn);

        ClientSendCmd.deviceStatusResponse(fromDevice, toDevice, deviceStatus.getOnline());
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
