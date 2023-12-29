package io.github.lunasaw.gbproxy.server.transimit.request.message;

import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MediaStatusNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MobilePositionNotify;
import io.github.lunasaw.gb28181.common.entity.response.DeviceRecord;
import io.github.lunasaw.gb28181.common.entity.response.DeviceResponse;


/**
 * @author luna
 * @date 2023/10/21
 */
public interface MessageProcessorServer {

    /**
     * 更新设备心跳信息
     * @param deviceKeepLiveNotify
     */
    void keepLiveDevice(DeviceKeepLiveNotify deviceKeepLiveNotify);

    /**
     * 更新设备地址信息
     *
     * @param userId
     * @param remoteAddressInfo
     */
    void updateRemoteAddress(String userId, RemoteAddressInfo remoteAddressInfo);

    /**
     * 更新报警信息
     * @param deviceAlarmNotify
     */
    void updateDeviceAlarm(DeviceAlarmNotify deviceAlarmNotify);

    /**
     * 更新位置信息
     * @param mobilePositionNotify
     */
    void updateMobilePosition(MobilePositionNotify mobilePositionNotify);

    /**
     * 更新媒体状态
     * @param mediaStatusNotify
     */
    void updateMediaStatus(MediaStatusNotify mediaStatusNotify);

    /**
     * 更新设备录像
     * 
     * @param userId
     * @param deviceRecord
     */
    void updateDeviceRecord(String userId, DeviceRecord deviceRecord);

    /**
     * 更新设备通道
     * 
     * @param userId
     * @param deviceResponse
     */
    void updateDeviceResponse(String userId, DeviceResponse deviceResponse);
}
