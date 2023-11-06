package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.enums.DeviceControlType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

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
    public String getRootType() {
        return CONTROL;
    }

    @Override
    public void handForEvt(RequestEvent event) {


        DeviceControlType deviceControlType = DeviceControlType.getDeviceControlTypeFilter(getXmlStr());

        if (deviceControlType == null) {
            return;
        }

        Object o = parseXml(deviceControlType.getClazz());

        messageProcessorClient.deviceControl(o);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
