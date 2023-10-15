package io.github.lunasaw.gbproxy.client.transmit.cmd;

import java.util.List;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.client.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.gbproxy.client.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gbproxy.client.entity.response.DeviceInfo;
import io.github.lunasaw.gbproxy.client.entity.response.DeviceItem;
import io.github.lunasaw.gbproxy.client.entity.response.DeviceResponse;
import io.github.lunasaw.gbproxy.client.entity.response.DeviceStatus;
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
    public static String deviceKeepLive(FromDevice fromDevice, ToDevice toDevice, String status) {
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
    public static String deviceChanelCatlog(FromDevice fromDevice, ToDevice toDevice, List<DeviceItem> deviceItems) {
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
    public static String deviceInfo(FromDevice fromDevice, ToDevice toDevice, DeviceInfo deviceInfo) {
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
    public static String deviceStatus(FromDevice fromDevice, ToDevice toDevice, String online) {

        DeviceStatus deviceStatus =
                new DeviceStatus(CmdTypeEnum.DEVICE_STATUS.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());

        deviceStatus.setStatus("ok");
        deviceStatus.setResult("ok");
        deviceStatus.setOnline(online);

        return SipSender.doMessageRequest(fromDevice, toDevice, deviceStatus);
    }


}
