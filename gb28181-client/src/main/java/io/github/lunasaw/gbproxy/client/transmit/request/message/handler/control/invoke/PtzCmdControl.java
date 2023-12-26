package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.invoke;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gb28181.common.entity.control.DeviceControlPtz;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.DeviceControlCmd;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component("ptzCmdControl")
public abstract class PtzCmdControl implements DeviceControlCmd {
    @Override
    public void doCmd(Object o) {
        DeviceControlPtz deviceControlPtz = (DeviceControlPtz)o;
    }
}
