package io.github.lunasaw.gbproxy.server.transimit.request.notify;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceOtherUpdateNotify;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

public interface NotifyProcessorServer extends SipUserGenerate {

    void deviceNotifyUpdate(String userId, DeviceOtherUpdateNotify deviceOtherUpdateNotify);
}