package io.github.lunasaw.gbproxy.server.transimit.response.subscribe;

import io.github.lunasaw.sip.common.entity.response.DeviceSubscribe;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author weidian
 * @date 2023/10/21
 */
public interface SubscribeResponseProcessorServer extends SipUserGenerate {
    void subscribeResult(DeviceSubscribe deviceSubscribe);

}
