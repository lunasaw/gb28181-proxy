package io.github.lunasaw.gbproxy.test.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorUser;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

/**
 * @author luna
 * @date 2023/10/17
 */
@Component
public class DefaultRegisterProcessorUser implements RegisterProcessorUser {

    @Autowired
    private FromDevice fromDevice;
    @Autowired
    private ToDevice toDevice;

    @Override
    public Integer getExpire(String userId) {
        return RegisterProcessorUser.super.getExpire(userId);
    }

    @Override
    public ToDevice getToDevice(String userId) {
        return toDevice;
    }

    @Override
    public FromDevice getFromDevice(String userId) {
        return fromDevice;
    }
}
