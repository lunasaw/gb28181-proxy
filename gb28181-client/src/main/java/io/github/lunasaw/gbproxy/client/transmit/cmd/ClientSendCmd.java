package io.github.lunasaw.gbproxy.client.transmit.cmd;

import java.util.List;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.client.entity.notify.*;
import io.github.lunasaw.gbproxy.client.entity.response.*;
import io.github.lunasaw.sip.common.entity.DeviceAlarm;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.transmit.SipSender;

/**
 * @author luna
 * @date 2023/10/15
 */
public class ClientSendCmd {

    /**
     * 告警上报
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceAlarmNotify(FromDevice fromDevice, ToDevice toDevice, DeviceAlarm deviceAlarm) {
        DeviceAlarmNotify deviceAlarmNotify = new DeviceAlarmNotify(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceAlarmNotify.setAlarm(deviceAlarm);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceAlarmNotify);
    }

    /**
     * 上报状态
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param status
     * @return
     */
    public static String deviceKeepLiveNotify(FromDevice fromDevice, ToDevice toDevice, String status) {
        DeviceKeepLiveNotify deviceKeepLiveNotify =
            new DeviceKeepLiveNotify(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceKeepLiveNotify.setStatus(status);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceKeepLiveNotify);
    }

    /**
     * 上报设备信息
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param deviceItems 通道状态
     * @return
     */
    public static String deviceChannelCatlogResponse(FromDevice fromDevice, ToDevice toDevice, List<DeviceItem> deviceItems) {
        DeviceResponse deviceResponse =
            new DeviceResponse(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceResponse.setSumNum(deviceItems.size());
        deviceResponse.setDeviceItemList(deviceItems);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceResponse);
    }

    /**
     * 向上级回复DeviceInfo查询信息
     * @param fromDevice
     * @param toDevice
     * @param deviceInfo
     * @return
     */
    public static String deviceInfoResponse(FromDevice fromDevice, ToDevice toDevice, DeviceInfo deviceInfo) {
        deviceInfo.setCmdType(CmdTypeEnum.DEVICE_INFO.getType());
        deviceInfo.setSn(RandomStrUtil.getValidationCode());
        deviceInfo.setDeviceId(toDevice.getUserId());
        return SipSender.doMessageRequest(fromDevice, toDevice, deviceInfo);
    }

    /**
     * 推送设备状态信息
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param online "ONLINE":"OFFLINE"
     * @return
     */
    public static String deviceStatusResponse(FromDevice fromDevice, ToDevice toDevice, String online) {

        DeviceStatus deviceStatus =
                new DeviceStatus(CmdTypeEnum.DEVICE_STATUS.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceStatus.setStatus("ok");
        deviceStatus.setResult("ok");
        deviceStatus.setOnline(online);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceStatus);
    }

    /**
     * 设备位置推送
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param mobilePositionNotify
     * @return
     */
    public static String MobilePositionNotify(FromDevice fromDevice, ToDevice toDevice, MobilePositionNotify mobilePositionNotify) {
        mobilePositionNotify.setCmdType(CmdTypeEnum.DEVICE_INFO.getType());
        mobilePositionNotify.setSn(RandomStrUtil.getValidationCode());
        mobilePositionNotify.setDeviceId(toDevice.getUserId());
        return SipSender.doMessageRequest(fromDevice, toDevice, mobilePositionNotify);
    }

    /**
     * 设备通道更新通知
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param deviceItems 通道列表
     * @return
     */
    public static String deviceChannelUpdateCatlog(FromDevice fromDevice, ToDevice toDevice, List<DeviceUpdateItem> deviceItems) {
        DeviceUpdateNotify deviceUpdateNotify =
            new DeviceUpdateNotify(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceUpdateNotify.setSumNum(deviceItems.size());
        deviceUpdateNotify.setDeviceItemList(deviceItems);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceUpdateNotify);
    }


    /**
     * 事件更新推送
     *
     * @param fromDevice  发送设备
     * @param toDevice    接收设备
     * @param deviceItems 推送事件
     * @return
     */
    public static String deviceOtherUpdateCatlog(FromDevice fromDevice, ToDevice toDevice, List<DeviceOtherUpdateNotify.OtherItem> deviceItems) {
        DeviceOtherUpdateNotify deviceUpdateNotify =
                new DeviceOtherUpdateNotify(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceUpdateNotify.setSumNum(deviceItems.size());
        deviceUpdateNotify.setDeviceItemList(deviceItems);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceUpdateNotify);
    }

    /**
     * 设备录像上报
     *
     * @param fromDevice        发送设备
     * @param toDevice          接收设备
     * @param deviceRecordItems 录像文件
     * @return
     */
    public static String deviceCatalogResponse(FromDevice fromDevice, ToDevice toDevice, List<DeviceRecord.RecordItem> deviceRecordItems) {
        DeviceRecord deviceRecord =
                new DeviceRecord(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceRecord.setSumNum(deviceRecordItems.size());
        deviceRecord.setRecordList(deviceRecordItems);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceRecord);
    }

}
