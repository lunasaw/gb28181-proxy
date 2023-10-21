package io.github.lunasaw.gbproxy.test.user.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterInfo;
import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.SipTransaction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/10/18
 */
@Slf4j
@Component
public class DefaultRegisterProcessorServer implements RegisterProcessorServer {

    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;

    @Autowired
    @Qualifier("serverTo")
    private Device toDevice;

    @Override
    public SipTransaction getTransaction(String userId) {
        return null;
    }

    @Override
    public void updateRegisterInfo(String userId, RegisterInfo registerInfo) {
        log.info("updateRegisterInfo::userId = {}, registerInfo = {}", userId, registerInfo);
    }

    @Override
    public void updateSipTransaction(String userId, SipTransaction sipTransaction) {
        log.info("updateSipTransaction::userId = {}, sipTransaction = {}", userId, sipTransaction);
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
