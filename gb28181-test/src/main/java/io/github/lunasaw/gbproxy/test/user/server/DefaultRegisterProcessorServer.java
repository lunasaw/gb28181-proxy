package io.github.lunasaw.gbproxy.test.user.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterInfo;
import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.SipTransaction;
import io.github.lunasaw.sip.common.entity.ToDevice;
import lombok.extern.slf4j.Slf4j;

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

    public static Map<String, SipTransaction> sipTransactionMap = new ConcurrentHashMap<>();

    public static Map<String, Device>         deviceMap         = new ConcurrentHashMap<>();

    @Override
    public SipTransaction getTransaction(String userId) {
        return sipTransactionMap.get(userId);
    }

    @Override
    public void updateRegisterInfo(String userId, RegisterInfo registerInfo) {

        ToDevice instance = ToDevice.getInstance(userId, registerInfo.getLocalIp(), registerInfo.getRemotePort());

        deviceMap.put(userId, instance);

        log.info("设备注册更新::userId = {}, registerInfo = {}", userId, JSON.toJSONString(registerInfo));
    }

    @Override
    public void updateSipTransaction(String userId, SipTransaction sipTransaction) {
        log.info("设备注册::userId = {}, sipTransaction = {}", userId, sipTransaction);
        sipTransactionMap.put(userId, sipTransaction);
    }

    @Override
    public void deviceOffLine(String userId, RegisterInfo registerInfo, SipTransaction sipTransaction) {
        log.info("设备注销::userId = {}, sipTransaction = {}", userId, sipTransaction);
        deviceMap.remove(userId);
        sipTransactionMap.remove(userId);
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
