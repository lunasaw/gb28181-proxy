package io.github.lunasaw.gbproxy.server.transimit.cmd;

import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.server.entity.DeviceQuery;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.transmit.SipRequestProvider;
import io.github.lunasaw.sip.common.transmit.SipSender;

import javax.sip.message.Request;

/**
 * @author weidian
 * @date 2023/10/14
 */
public class DeviceQueryCmd {

    /**
     * 设备信息查询
     * @param fromDevice 发送设备
     * @param toDevice 接收设备
     * @return callId
     */
    public static String deviceInfoQuery(FromDevice fromDevice, ToDevice toDevice) {
        DeviceQuery deviceQuery =new DeviceQuery(CmdTypeEnum.Device_Info.getType(), RandomStrUtil.getValidationCode(), toDevice.getUserId());
        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SipRequestProvider.createMessageRequest(fromDevice, toDevice, deviceQuery.toString(), callId);
        SipSender.transmitRequest(fromDevice.getIp(), messageRequest);
        return callId;
    }

    public static void main(String[] args) {

    }
}
