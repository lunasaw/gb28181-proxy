package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.invoke;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.DeviceControlCmd;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component("recordCmdControl")
public abstract class RecordCmdControl implements DeviceControlCmd {
    @Override
    public void doCmd(Object o) {

    }
}
