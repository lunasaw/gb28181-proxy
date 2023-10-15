package io.github.lunasaw.gbproxy.client.transmit.cmd;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.client.entity.notify.DeviceKeepLive;
import io.github.lunasaw.gbproxy.client.entity.notify.DeviceNotifyAlarm;
import io.github.lunasaw.sip.common.entity.DeviceAlarm;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.transmit.SipSender;

/**
 * @author weidian
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
        DeviceNotifyAlarm deviceNotifyAlarm = new DeviceNotifyAlarm(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceNotifyAlarm.setAlarm(deviceAlarm);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceNotifyAlarm);
    }

    /**
     * 上报状态
     * 
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @param status
     * @return
     */
    public static String deviceKeepLive(FromDevice fromDevice, ToDevice toDevice, String status) {
        DeviceKeepLive deviceKeepLive =
            new DeviceKeepLive(CmdTypeEnum.DEVICE_INFO.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceKeepLive.setStatus(status);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceKeepLive);
    }

}
