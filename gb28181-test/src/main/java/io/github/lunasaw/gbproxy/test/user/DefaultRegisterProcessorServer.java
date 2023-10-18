package io.github.lunasaw.gbproxy.test.user;

import org.springframework.beans.factory.annotation.Autowired;

import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterInfo;
import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.SipTransaction;

/**
 * @author luna
 * @date 2023/10/18
 */
public class DefaultRegisterProcessorServer implements RegisterProcessorServer {

    @Autowired
    private Device fromDevice;
    @Autowired
    private Device toDevice;

    @Override
    public SipTransaction getTransaction(String userId) {
        return null;
    }

    @Override
    public void updateRegisterInfo(String userId, RegisterInfo registerInfo) {

    }

    @Override
    public void updateSipTransaction(String userId, SipTransaction sipTransaction) {

    }

    @Override
    public Device getToDevice(String userId) {
        return fromDevice;
    }

    @Override
    public Device getFromDevice(String userId) {
        return toDevice;
    }
}
