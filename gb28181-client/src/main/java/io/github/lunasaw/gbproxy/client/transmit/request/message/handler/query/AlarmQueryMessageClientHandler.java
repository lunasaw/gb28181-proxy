package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.query;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.sip.common.entity.query.DeviceAlarmQuery;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 响应设备告警查询
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class AlarmQueryMessageClientHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "Alarm";

    public AlarmQueryMessageClientHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
    }

    @Override
    public String getRootType() {
        return QUERY;
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();
        String sipId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)messageProcessorClient.getFromDevice();
        ToDevice toDevice = (ToDevice)messageProcessorClient.getToDevice(sipId);

        DeviceAlarmQuery deviceAlarmQuery = parseXml(DeviceAlarmQuery.class);

        DeviceAlarmQuery deviceAlarmQuery2 = parseRequest(event, fromDevice.getCharset(), DeviceAlarmQuery.class);

        // 请求序列化编号，上游后续处理
        String sn = deviceAlarmQuery.getSn();
        DeviceAlarmNotify deviceAlarmNotify = messageProcessorClient.getDeviceAlarmNotify(deviceAlarmQuery);
        deviceAlarmNotify.setSn(sn);

        ClientSendCmd.deviceAlarmNotify(fromDevice, toDevice, deviceAlarmNotify);
    }

    @Override
    public String getCmdType() {
        return CMD_TYPE;
    }
}
