package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.invoke;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.DeviceControlCmd;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component("telnetBootControl")
public abstract class TelnetBootControl implements DeviceControlCmd {
    @Override
    public void doCmd(Object o) {

    }
}
