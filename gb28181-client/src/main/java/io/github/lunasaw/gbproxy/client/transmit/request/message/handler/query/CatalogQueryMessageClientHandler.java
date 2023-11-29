package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.response.DeviceResponse;
import lombok.Setter;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备目录查询
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class CatalogQueryMessageClientHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String cmdType = CMD_TYPE;

    public CatalogQueryMessageClientHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
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
        FromDevice fromDevice = (FromDevice)messageProcessorClient.getFromDevice();
        ToDevice toDevice = (ToDevice) messageProcessorClient.getToDevice(sipId);
        if (toDevice == null) {
            return;
        }

        DeviceQuery deviceQuery = parseXml(DeviceQuery.class);

        // 请求序列化编号，上游后续处理
        String sn = deviceQuery.getSn();
        DeviceResponse deviceResponse = messageProcessorClient.getDeviceItem(userId);
        deviceResponse.setSn(sn);

        ClientSendCmd.deviceChannelCatalogResponse(fromDevice, toDevice, deviceResponse);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
