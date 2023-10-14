package io.github.lunasaw.gbproxy.server.transimit.cmd;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.server.entity.DeviceBroadcast;
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

}
