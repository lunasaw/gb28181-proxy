package io.github.lunasaw.gbproxy.server.transimit.request.message.notify;

import javax.sip.RequestEvent;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageServerHandlerAbstract;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理设备告警信息
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class AlarmNotifyMessageHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "Alarm";

    public AlarmNotifyMessageHandler(MessageProcessorServer messageProcessorServer, SipUserGenerateServer sipUserGenerate) {
        super(messageProcessorServer, sipUserGenerate);
    }


    @Override
    public String getRootType() {
        return NOTIFY;
    }

    @Override
    public void handForEvt(RequestEvent event) {

        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();

        // 设备查询
        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(userId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return;
        }

        DeviceAlarmNotify deviceAlarmNotify = parseXml(DeviceAlarmNotify.class);

        messageProcessorServer.updateDeviceAlarm(deviceAlarmNotify);
    }

    @Override
    public String getCmdType() {
        return CMD_TYPE;
    }
}
