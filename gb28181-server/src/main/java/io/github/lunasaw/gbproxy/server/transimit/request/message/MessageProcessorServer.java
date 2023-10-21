package io.github.lunasaw.gbproxy.server.transimit.request.message;

import io.github.lunasaw.sip.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author weidian
 * @date 2023/10/21
 */
public interface MessageProcessorServer extends SipUserGenerate {

    void keepLiveDevice(DeviceKeepLiveNotify deviceKeepLiveNotify);
}
