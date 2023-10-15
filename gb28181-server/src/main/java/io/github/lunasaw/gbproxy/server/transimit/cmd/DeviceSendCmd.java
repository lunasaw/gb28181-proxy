package io.github.lunasaw.gbproxy.server.transimit.cmd;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.server.entity.*;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.transmit.SipSender;

/**
 * @author weidian
 * @date 2023/10/14
 */
public class DeviceSendCmd {

    /**
     * 设备信息查询
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceInfo(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        return SipSender.doRequest(fromDevice, toDevice, deviceQuery);
    }

    /**
     * 设备状态查询
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceStatus(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.DEVICE_STATUS.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        return SipSender.doRequest(fromDevice, toDevice, deviceQuery);
    }

    /**
     * 查询目录列表
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceCatalog(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.CATALOG.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        return SipSender.doRequest(fromDevice, toDevice, deviceQuery);
    }



    /**
     * 设备广播
     * 
     * @param fromDevice
     * @param toDevice
     * @return
     */
    public String deviceBroadcast(FromDevice fromDevice, ToDevice toDevice) {
        DeviceBroadcast deviceBroadcast =
            new DeviceBroadcast(CmdTypeEnum.BROADCAST.getType(), RandomStrUtil.getValidationCode(), fromDevice.getUserId(), toDevice.getUserId());

        return SipSender.doRequest(fromDevice, toDevice, deviceBroadcast);
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
        return SipSender.doRequest(fromDevice, toDevice, deviceControl);
    }

    /**
     * 强制关键帧命令,设备收到此命令应立刻发送一个IDR帧
     *
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return
     */
    public String deviceControl(FromDevice fromDevice, ToDevice toDevice) {
        DeviceControl deviceControl =
                new DeviceControl(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(), fromDevice.getUserId());
        deviceControl.setIFameCmd("Send");
        return SipSender.doRequest(fromDevice, toDevice, deviceControl);
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

        return SipSender.doRequest(fromDevice, toDevice, deviceControlAlarm);
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

        return SipSender.doRequest(fromDevice, toDevice, deviceControlPosition);
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

        DeviceConfig deviceConfig =
            new DeviceConfig(CmdTypeEnum.DEVICE_CONFIG.getType(), RandomStrUtil.getValidationCode(),
                fromDevice.getUserId());

        deviceConfig.setBasicParam(new DeviceConfig.BasicParam(name, expiration, heartBeatInterval, heartBeatCount));

        return SipSender.doRequest(fromDevice, toDevice, deviceConfig);
    }

}
