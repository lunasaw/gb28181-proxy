package io.github.lunasaw.gbproxy.client.transmit.request.subscribe;

import io.github.lunasaw.gb28181.common.entity.query.DeviceQuery;
import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;

import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;

/**
 * @author luna
 * @version 1.0
 * @date 2023/12/11
 * @description:
 */
public interface SubscribeProcessorClient {

    void putSubscribe(String userId, SubscribeInfo subscribeInfo);

    DeviceSubscribe getDeviceSubscribe(DeviceQuery deviceQuery);
}
