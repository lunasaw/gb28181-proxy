package io.github.lunasaw.gbproxy.test.user.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceOtherUpdateNotify;
import io.github.lunasaw.gbproxy.server.transimit.request.notify.NotifyProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/14
 * @description:
 */
@Component
public class DefaultNotifyProcessorServer implements NotifyProcessorServer {
    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;

    @Override
    public void deviceNotifyUpdate(String userId, DeviceOtherUpdateNotify deviceOtherUpdateNotify) {

    }
}
