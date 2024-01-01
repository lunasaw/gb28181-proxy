package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.notify;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.transmit.request.message.ClientMessageRequestProcessor;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceBroadcastNotify;

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

    private String cmdType = CMD_TYPE;

    public BroadcastNotifyMessageHandler(MessageProcessorClient messageProcessorClient, SipUserGenerateClient sipUserGenerateClient) {
        super(messageProcessorClient, sipUserGenerateClient);
    }


    @Override
    public String getRootType() {
        return ClientMessageRequestProcessor.METHOD + NOTIFY;
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
        if (fromDevice == null || !fromDevice.getUserId().equals(userId)) {
            throw new RuntimeException("查询设备失败");
        }

        DeviceBroadcastNotify broadcastNotify = parseXml(DeviceBroadcastNotify.class);

        messageProcessorClient.broadcastNotify(broadcastNotify);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
