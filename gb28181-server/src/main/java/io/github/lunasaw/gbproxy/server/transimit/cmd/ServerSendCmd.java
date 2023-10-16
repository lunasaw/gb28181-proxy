package io.github.lunasaw.gbproxy.server.transimit.cmd;

import java.util.Date;

import com.luna.common.date.DateUtils;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.server.entity.control.*;
import io.github.lunasaw.gbproxy.server.entity.notify.DeviceBroadcastNotify;
import io.github.lunasaw.gbproxy.server.entity.query.*;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.transmit.SipSender;

/**
 * @author luna
 * @date 2023/10/14
 */
public class ServerSendCmd {

    /**
     * 设备信息查询
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceInfo(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        return SipSender.doMessageRequest(fromDevice, toDevice, deviceQuery);
    }

    /**
     * 设备预设位置查询
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String devicePresetQuery(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.PRESET_QUERY.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        return SipSender.doMessageRequest(fromDevice, toDevice, deviceQuery);
    }

    /**
     * 查询移动设备位置数据
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String devicePresetQuery(FromDevice fromDevice, ToDevice toDevice, String interval) {
        DeviceMobileQuery deviceMobileQuery =
            new DeviceMobileQuery(CmdTypeEnum.MOBILE_POSITION.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        deviceMobileQuery.setInterval(interval);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceMobileQuery);
    }

    /**
     * 订阅移动设备位置数据
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String devicePresetSubscribe(FromDevice fromDevice, ToDevice toDevice, String interval, Integer expires, String eventType,
                                               String eventId) {
        DeviceMobileQuery deviceMobileQuery =
            new DeviceMobileQuery(CmdTypeEnum.MOBILE_POSITION.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        deviceMobileQuery.setInterval(interval);

        SubscribeInfo subscribeInfo = new SubscribeInfo();
        subscribeInfo.setEventId(eventId);
        subscribeInfo.setEventType(eventType);
        subscribeInfo.setExpires(expires);

        return SipSender.doSubscribeRequest(fromDevice, toDevice, deviceMobileQuery, subscribeInfo);
    }

    /**
     * 设备状态查询
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceStatusQuery(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.DEVICE_STATUS.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        return SipSender.doMessageRequest(fromDevice, toDevice, deviceQuery);
    }

    /**
     * 查询目录列表
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceCatalogQuery(FromDevice fromDevice, ToDevice toDevice, Date startTime, Date endTime, String secrecy, String type) {
        DeviceRecordQuery recordQuery = new DeviceRecordQuery(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        recordQuery.setStartTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, startTime));
        recordQuery.setEndTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, endTime));
        recordQuery.setSecrecy(secrecy);
        recordQuery.setType(type);

        return SipSender.doMessageRequest(fromDevice, toDevice, recordQuery);
    }

    /**
     * 会话订阅
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param expires 过期时间
     * @param eventType 事件类型
     * @return
     */
    public static String deviceCatalogSubscribe(FromDevice fromDevice, ToDevice toDevice,
                                                Integer expires, String eventType, String eventId) {
        DeviceQuery recordQuery = new DeviceQuery(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        SubscribeInfo subscribeInfo = new SubscribeInfo();
        subscribeInfo.setEventId(eventId);
        subscribeInfo.setEventType(eventType);
        subscribeInfo.setExpires(expires);

        return SipSender.doSubscribeRequest(fromDevice, toDevice, recordQuery, subscribeInfo);
    }

    /**
     * 告警查询
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param startPriority 报警起始级别（可选）
     * @param endPriority 报警终止级别（可选）
     * @param alarmMethod 报警方式条件（可选）
     * @param alarmType 报警类型
     * @param startTime 报警发生起始时间（可选）
     * @param endTime 报警发生终止时间（可选）
     * @return callId
     */
    public static String deviceAlarmQuery(FromDevice fromDevice, ToDevice toDevice, Date startTime, Date endTime, String startPriority,
        String endPriority, String alarmMethod, String alarmType) {
        DeviceAlarmQuery deviceAlarmQuery =
            new DeviceAlarmQuery(CmdTypeEnum.ALARM.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceAlarmQuery.setStartTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, startTime));
        deviceAlarmQuery.setEndTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, endTime));
        deviceAlarmQuery.setStartAlarmPriority(startPriority);
        deviceAlarmQuery.setEndAlarmPriority(endPriority);
        deviceAlarmQuery.setAlarmType(alarmType);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceAlarmQuery);
    }

    /**
     * 设备广播
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return
     */
    public String deviceBroadcast(FromDevice fromDevice, ToDevice toDevice) {
        DeviceBroadcastNotify deviceBroadcastNotify =
                new DeviceBroadcastNotify(CmdTypeEnum.BROADCAST.getType(), RandomStrUtil.getValidationCode(), fromDevice.getUserId(),
                        toDevice.getUserId());

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceBroadcastNotify);
    }

    /**
     * 报警布防/撤防命令
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param guardCmdStr "SetGuard"/"ResetGuard"
     * @return
     */
    public String deviceControl(FromDevice fromDevice, ToDevice toDevice, String guardCmdStr) {
        DeviceControl deviceControl =
            new DeviceControl(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(), fromDevice.getUserId());
        deviceControl.setGuardCmd(guardCmdStr);
        return SipSender.doMessageRequest(fromDevice, toDevice, deviceControl);
    }

    /**
     * 报警复位命令
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param alarmMethod 方式
     * @param alarmType 类型
     * @return
     */
    public String deviceControlAlarm(FromDevice fromDevice, ToDevice toDevice, String alarmMethod, String alarmType) {

        DeviceControlAlarm deviceControlAlarm = new DeviceControlAlarm(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(),
            fromDevice.getUserId());

        deviceControlAlarm.setAlarmCmd("ResetAlarm");
        deviceControlAlarm.setAlarmInfo(new DeviceControlAlarm.AlarmInfo(alarmMethod, alarmType));

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceControlAlarm);
    }

    /**
     *
     * 看守位控制命令
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param enable 看守位使能：1 = 开启，0 = 关闭
     * @param resetTime 自动归位时间间隔，开启看守位时使用，单位:秒(s)
     * @param presetIndex 调用预置位编号，开启看守位时使用，取值范围0~255
     * @return
     */
    public String deviceControlAlarm(FromDevice fromDevice, ToDevice toDevice, String enable, String resetTime, String presetIndex) {

        DeviceControlPosition deviceControlPosition =
            new DeviceControlPosition(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(),
                fromDevice.getUserId(), new DeviceControlPosition.HomePosition(enable, resetTime, presetIndex));

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceControlPosition);
    }

    /**
     * 设备配置命令：basicParam
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param name 设备/通道名称（可选）
     * @param expiration 注册过期时间（可选）
     * @param heartBeatInterval 心跳间隔时间（可选）
     * @param heartBeatCount 心跳超时次数（可选）
     * @return
     */
    public String deviceConfig(FromDevice fromDevice, ToDevice toDevice, String name, String expiration,
        String heartBeatInterval, String heartBeatCount) {

        DeviceConfigControl deviceConfigControl =
            new DeviceConfigControl(CmdTypeEnum.DEVICE_CONFIG.getType(), RandomStrUtil.getValidationCode(),
                fromDevice.getUserId());

        deviceConfigControl.setBasicParam(new DeviceConfigControl.BasicParam(name, expiration, heartBeatInterval, heartBeatCount));

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceConfigControl);
    }

    /**
     * 下载设备配置
     * 
     * @param fromDevice
     * @param toDevice
     * @param configType 配置类型
     * @return
     */
    public String deviceConfigDownload(FromDevice fromDevice, ToDevice toDevice, String configType) {

        DeviceConfigDownload deviceConfig =
            new DeviceConfigDownload(CmdTypeEnum.CONFIG_DOWNLOAD.getType(), RandomStrUtil.getValidationCode(),
                fromDevice.getUserId());

        deviceConfig.setConfigType(configType);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceConfig);
    }

    /**
     * 强制关键帧命令,设备收到此命令应立刻发送一个IDR帧
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return
     */
    public String deviceControlIdr(FromDevice fromDevice, ToDevice toDevice) {
        DeviceControl deviceControl =
                new DeviceControl(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(), fromDevice.getUserId());
        deviceControl.setIFameCmd("Send");
        return SipSender.doMessageRequest(fromDevice, toDevice, deviceControl);
    }

    /**
     * 伸缩控制
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param dragZoomIn 放大
     * @param dragZoomOut 缩小
     * @return
     */
    public String deviceControlDrag(FromDevice fromDevice, ToDevice toDevice, DeviceControlDrag.DragZoom dragZoomIn,
                                    DeviceControlDrag.DragZoom dragZoomOut) {
        DeviceControlDrag deviceControlDrag =
                new DeviceControlDrag(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(), fromDevice.getUserId());

        deviceControlDrag.setDragZoomIn(dragZoomIn);
        deviceControlDrag.setDragZoomOut(dragZoomOut);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceControlDrag);
    }

    /**
     * 回复ACK
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @return
     */
    public String deviceAck(FromDevice fromDevice, ToDevice toDevice) {
        return SipSender.doAckRequest(fromDevice, toDevice);
    }
}
