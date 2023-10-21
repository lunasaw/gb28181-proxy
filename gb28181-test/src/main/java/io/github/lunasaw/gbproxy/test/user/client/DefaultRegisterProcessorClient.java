package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorClient;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

/**
 * @author luna
 * @date 2023/10/17
 */
@Component
public class DefaultRegisterProcessorClient implements RegisterProcessorClient {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;
    @Autowired
    @Qualifier("clientTo")
    private Device toDevice;

    @Override
    public Integer getExpire(String userId) {
        return RegisterProcessorClient.super.getExpire(userId);
    }

    @Override
    public Device getToDevice(String userId) {
        return toDevice;
    }

    @Override
    public Device getFromDevice(String userId) {
        return fromDevice;
    }
}
