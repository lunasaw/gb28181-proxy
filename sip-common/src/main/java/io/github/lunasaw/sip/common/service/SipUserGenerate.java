package io.github.lunasaw.sip.common.service;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/10/17
 */
public interface SipUserGenerate {

    /**
     * 接受设备
     *
     * @param userId sip用户id
     * @return ToDevice
     */
    Device getToDevice(String userId);

    /**
     * 发送设备
     *
     * @return FromDevice
     */
    Device getFromDevice();
}
