package io.github.lunasaw.gbproxy.server.transimit.request.register;

import io.github.lunasaw.sip.common.entity.SipTransaction;

/**
 * @author luna
 * @date 2023/12/29
 */
public class CustomRegisterProcessorServer implements RegisterProcessorServer {
    @Override
    public void responseUnauthorized(String userId) {

    }

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
    public void deviceOffLine(String userId, RegisterInfo registerInfo, SipTransaction sipTransaction) {

    }
}
