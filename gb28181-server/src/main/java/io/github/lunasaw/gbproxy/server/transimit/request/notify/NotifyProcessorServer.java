package io.github.lunasaw.gbproxy.server.transimit.request.notify;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceOtherUpdateNotify;


public interface NotifyProcessorServer {

    void deviceNotifyUpdate(String userId, DeviceOtherUpdateNotify deviceOtherUpdateNotify);
}