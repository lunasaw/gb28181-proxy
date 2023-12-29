package io.github.lunasaw.gbproxy.server.transimit.request.register;

import io.github.lunasaw.sip.common.entity.SipTransaction;


/**
 * @author luna
 * @date 2023/10/18
 */
public interface RegisterProcessorServer {

    /**
     * 获取第一次注册的事务
     *
     * @param userId 设备Id
     * @return
     */
    SipTransaction getTransaction(String userId);

    /**
     * 更新设备注册信息
     *
     * @param userId       设备Id
     * @param registerInfo
     */
    void updateRegisterInfo(String userId, RegisterInfo registerInfo);

    /**
     * 更新事务信息 = 设备上线
     *
     * @param userId
     * @param sipTransaction
     */
    void updateSipTransaction(String userId, SipTransaction sipTransaction);

    /**
     * 更新事务信息 = 设备下线
     *
     * @param userId
     * @param sipTransaction
     */
    void deviceOffLine(String userId, RegisterInfo registerInfo, SipTransaction sipTransaction);
}
