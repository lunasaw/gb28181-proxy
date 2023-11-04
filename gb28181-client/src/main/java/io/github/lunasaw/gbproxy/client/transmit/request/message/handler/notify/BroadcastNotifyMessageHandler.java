package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.notify;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.notify.DeviceBroadcastNotify;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class BroadcastNotifyMessageHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "Broadcast";

    private String             cmdType  = CMD_TYPE;

    public BroadcastNotifyMessageHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
    }

    @Override
    public String getRootType() {
        return Notify;
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)messageProcessorClient.getFromDevice(userId);

        DeviceBroadcastNotify broadcastNotify = parseRequest(event, fromDevice.getCharset(), DeviceBroadcastNotify.class);

        messageProcessorClient.broadcastNotify(broadcastNotify);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
