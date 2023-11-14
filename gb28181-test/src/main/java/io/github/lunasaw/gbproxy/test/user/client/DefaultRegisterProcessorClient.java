package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luna
 * @date 2023/10/17
 */
@Component
public class DefaultRegisterProcessorClient implements RegisterProcessorClient {

    public static Map<String, Device> deviceMap = new ConcurrentHashMap<>();


    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Override
    public Integer getExpire(String userId) {
        return RegisterProcessorClient.super.getExpire(userId);
    }

    @Override
    public Device getToDevice(String userId) {
        return deviceMap.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
