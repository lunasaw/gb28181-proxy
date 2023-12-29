package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.gb28181.common.entity.base.DeviceSession;
import io.github.lunasaw.gb28181.common.entity.query.DeviceConfigDownload;
import io.github.lunasaw.gb28181.common.entity.response.DeviceConfigResponse;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备配置查询
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class ConfigDownloadMessageHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "ConfigDownload";

    private String             cmdType  = CMD_TYPE;

    public ConfigDownloadMessageHandler(MessageProcessorClient messageProcessorClient, SipUserGenerate sipUserGenerate) {
        super(messageProcessorClient, sipUserGenerate);
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
        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(sipId);

        DeviceConfigDownload deviceConfigDownload = parseXml(DeviceConfigDownload.class);

        DeviceConfigResponse deviceConfigResponse = messageProcessorClient.getDeviceConfigResponse(deviceConfigDownload);
        deviceConfigResponse.setSn(deviceConfigDownload.getSn());

        ClientSendCmd.deviceConfigResponse(fromDevice, toDevice, deviceConfigResponse);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
