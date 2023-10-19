package io.github.lunasaw.gbproxy.client.transmit.request.message;

import io.github.lunasaw.gbproxy.client.entity.response.DeviceInfo;
import io.github.lunasaw.sip.common.entity.SipTransaction;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/10/18
 */
public interface MessageProcessorClient extends SipUserGenerate {

    /**
     * 获取设备信息
     * DeviceInfo
     * {@link io.github.lunasaw.gbproxy.client.transmit.request.message.handler.DeviceInfoQueryMessageHandler}
     *
     * @param userId
     * @return
     */
    DeviceInfo getDeviceInfo(String userId);
}
