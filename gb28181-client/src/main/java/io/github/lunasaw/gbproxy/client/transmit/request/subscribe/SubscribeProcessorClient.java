package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
import io.github.lunasaw.sip.common.entity.response.DeviceSubscribe;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/11
 * @description:
 */
public interface SubscribeProcessorClient extends SipUserGenerate {

    void putSubscribe(String userId, SubscribeInfo subscribeInfo);

    DeviceSubscribe getDeviceSubscribe(DeviceQuery deviceQuery);
}
