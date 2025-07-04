package io.github.lunasaw.gbproxy.server.transimit.response.subscribe;

import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;

/**
 * @author luna
 * @date 2023/10/21
 */
public interface SubscribeResponseProcessorServer {

    void responseSubscribe(DeviceSubscribe deviceSubscribe);

}
