package io.github.lunasaw.gbproxy.client.transmit.request.message;

import io.github.lunasaw.sip.common.entity.response.DeviceInfo;
import io.github.lunasaw.sip.common.entity.response.DeviceItem;
import io.github.lunasaw.sip.common.entity.response.DeviceStatus;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

import java.util.List;

/**
 * @author luna
 * @date 2023/10/18
 */
public interface MessageProcessorClient extends SipUserGenerate {

    /**
     * 获取设备信息
     * DeviceInfo
     * {@link io.github.lunasaw.gbproxy.client.transmit.request.message.handler.DeviceStatusQueryMessageHandler}
     *
     * @param userId 设备Id
     * @return DeviceInfo
     */
    DeviceStatus getDeviceStatus(String userId);

    /**
     * 获取设备信息
     * DeviceInfo
     * {@link io.github.lunasaw.gbproxy.client.transmit.request.message.handler.DeviceInfoQueryMessageHandler}
     *
     * @param userId 设备Id
     * @return DeviceInfo
     */
    DeviceInfo getDeviceInfo(String userId);

    /**
     * 获取设备通道信息
     * {@link io.github.lunasaw.gbproxy.client.transmit.request.message.handler.CatalogQueryMessageHandler}
     *
     * @param userId
     * @return
     */
    List<DeviceItem> getDeviceItem(String userId);

}
