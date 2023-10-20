package io.github.lunasaw.gbproxy.client.transmit.request.message.handler;

import java.util.List;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
import io.github.lunasaw.sip.common.entity.response.DeviceItem;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
public class CatalogQueryMessageHandler extends MessageHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String cmdType = CMD_TYPE;

    public CatalogQueryMessageHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = responseAck(event);

        String userId = deviceSession.getUserId();
        String sipId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice(userId);
        ToDevice toDevice = (ToDevice) messageProcessorClient.getToDevice(sipId);

        DeviceQuery deviceQuery = parseRequest(event, fromDevice.getCharset(), DeviceQuery.class);

        // 请求序列化，上游后续处理
        String sn = deviceQuery.getSn();

        List<DeviceItem> deviceItem = messageProcessorClient.getDeviceItem(userId);

        ClientSendCmd.deviceChannelCatalogResponse(fromDevice, toDevice, deviceItem, sn);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
