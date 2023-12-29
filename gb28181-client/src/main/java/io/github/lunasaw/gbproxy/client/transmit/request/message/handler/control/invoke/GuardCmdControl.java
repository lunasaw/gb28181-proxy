package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.invoke;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.emums.DeviceControlType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.DeviceControlCmd;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component("guardCmdControl")
public class GuardCmdControl implements DeviceControlCmd, InitializingBean {
    @Override
    public void doCmd(Object o) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        DeviceControlType.GUARD.setBeanName("guardCmdControl");
    }
}
