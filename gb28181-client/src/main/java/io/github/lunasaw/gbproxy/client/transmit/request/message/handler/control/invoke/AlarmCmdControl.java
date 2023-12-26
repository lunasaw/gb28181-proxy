package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.invoke;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.DeviceControlCmd;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component("alarmCmdControl")
public abstract class AlarmCmdControl implements DeviceControlCmd {
    @Override
    public void doCmd(Object o) {

    }
}
