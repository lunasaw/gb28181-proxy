package io.github.lunasaw.sip.common.service;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/10/17
 */
@Component
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
