package io.github.lunasaw.gbproxy.client.transmit.request.message;

import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.notify.BroadcastNotifyMessageHandler;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query.CatalogQueryMessageClientHandler;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query.DeviceInfoQueryMessageClientHandler;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query.DeviceStatusQueryMessageClientHandler;
import io.github.lunasaw.sip.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.sip.common.entity.notify.DeviceBroadcastNotify;
import io.github.lunasaw.sip.common.entity.query.DeviceAlarmQuery;
import io.github.lunasaw.sip.common.entity.query.DeviceConfigDownload;
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

    /**
     * 语音广播通知
     * {@link BroadcastNotifyMessageHandler}
     *
     * @param broadcastNotify
     * @return
     */
    void broadcastNotify(DeviceBroadcastNotify broadcastNotify);

    /**
     * 设备告警通知
     * 
     * @param deviceAlarmQuery
     * @return
     */
    DeviceAlarmNotify getDeviceAlarmNotify(DeviceAlarmQuery deviceAlarmQuery);

    /**
     * 设备配置查询
     * 
     * @param deviceConfigDownload
     * @return
     */
    DeviceConfigResponse getDeviceConfigResponse(DeviceConfigDownload deviceConfigDownload);
}
