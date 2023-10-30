package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
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
public class DeviceControlMessageHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "DeviceControl";

    private String cmdType = CMD_TYPE;


    public DeviceControlMessageHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
    }

    @Override
    public void handForEvt(RequestEvent event) {

        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();

        // 设备查询
        FromDevice fromDevice = (FromDevice) messageProcessorClient.getFromDevice(userId);

//        DeviceControl deviceControl = parseRequest(event, fromDevice.getCharset(), DeviceControl.class);


        log.info("handForEvt::event = {}", event);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
