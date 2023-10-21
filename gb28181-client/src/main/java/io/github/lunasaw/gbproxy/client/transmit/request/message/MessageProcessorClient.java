package io.github.lunasaw.gbproxy.client.transmit.request.message;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query.CatalogQueryMessageClientHandler;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query.DeviceInfoQueryMessageClientHandler;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query.DeviceStatusQueryMessageClientHandler;
import io.github.lunasaw.sip.common.entity.query.DeviceRecordQuery;
import io.github.lunasaw.sip.common.entity.response.*;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/10/18
 */
public interface MessageProcessorClient extends SipUserGenerate {

    /**
     * 获取设备录像信息
     * DeviceRecord
     * {@link DeviceInfoQueryMessageClientHandler}
     *
     * @param deviceRecordQuery 设备Id
     * @return DeviceInfo
     */
    DeviceRecord getDeviceRecord(DeviceRecordQuery deviceRecordQuery);

    /**
     * 获取设备信息
     * DeviceStatus
     * {@link DeviceStatusQueryMessageClientHandler}
     *
     * @param userId 设备Id
     * @return DeviceInfo
     */
    DeviceStatus getDeviceStatus(String userId);

    /**
     * 获取设备信息
     * DeviceInfo
     * {@link DeviceInfoQueryMessageClientHandler}
     *
     * @param userId 设备Id
     * @return DeviceInfo
     */
    DeviceInfo getDeviceInfo(String userId);

    /**
     * 获取设备通道信息
     * {@link CatalogQueryMessageClientHandler}
     *
     * @param userId
     * @return
     */
    DeviceResponse getDeviceItem(String userId);
}
