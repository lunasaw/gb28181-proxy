package io.github.lunasaw.gbproxy.test.user.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.subscribe.SubscribeProcessorClient;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.gb28181.common.entity.query.DeviceQuery;
import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;
import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.subscribe.SubscribeHolder;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;

/**
 * @author weidian
 * @version 1.0
 * @date 2023/12/11
 * @description:
 */
@Component
public class DefaultSubscribeProcessorClient implements SubscribeProcessorClient {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Autowired
    private SubscribeHolder subscribeHolder;

    @Override
    public Device getToDevice(String userId) {
        return DeviceConfig.DEVICE_CLIENT_VIEW_MAP.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }

    @Override
    public void putSubscribe(String userId, SubscribeInfo subscribeInfo) {
        subscribeHolder.putCatalogSubscribe(userId, subscribeInfo);
    }

    @Override
    public DeviceSubscribe getDeviceSubscribe(DeviceQuery deviceQuery) {
        DeviceSubscribe deviceSubscribe = new DeviceSubscribe();
        deviceSubscribe.setDeviceId(deviceQuery.getDeviceId());
        deviceSubscribe.setCmdType(CmdTypeEnum.CATALOG.getType());
        deviceSubscribe.setSn(deviceQuery.getSn());
        return deviceSubscribe;
    }
}
