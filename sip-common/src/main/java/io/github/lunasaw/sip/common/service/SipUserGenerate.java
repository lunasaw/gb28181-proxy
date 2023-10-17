package io.github.lunasaw.sip.common.service;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

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
    ToDevice getToDevice(String userId);

    /**
     * 发送设备
     *
     * @param userId 用户id
     * @return FromDevice
     */
    FromDevice getFromDevice(String userId);
}
