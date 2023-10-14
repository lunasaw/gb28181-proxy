package io.github.lunasaw.gbproxy.server.transimit.cmd;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.server.entity.DeviceBroadcast;
import io.github.lunasaw.gbproxy.server.entity.DeviceControl;
import io.github.lunasaw.gbproxy.server.entity.DeviceControlAlarm;
import io.github.lunasaw.gbproxy.server.entity.DeviceQuery;
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
    public static String deviceInfoQuery(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery = new DeviceQuery(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
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

    public static void main(String[] args) {

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
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param alarmMethod
     * @param alarmType
     * @return
     */
    public String deviceControlAlarm(FromDevice fromDevice, ToDevice toDevice, String alarmMethod, String alarmType) {

        DeviceControlAlarm deviceControlAlarm = new DeviceControlAlarm(CmdTypeEnum.DEVICE_CONTROL.getType(), RandomStrUtil.getValidationCode(),
            fromDevice.getUserId(), "ResetAlarm", new DeviceControlAlarm.AlarmInfo(alarmMethod, alarmType));

        return SipSender.doRequest(fromDevice, toDevice, deviceControlAlarm);
    }

}
