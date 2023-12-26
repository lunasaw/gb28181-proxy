package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control;

import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component
public interface DeviceControlCmd {

    void doCmd(Object o);

}
