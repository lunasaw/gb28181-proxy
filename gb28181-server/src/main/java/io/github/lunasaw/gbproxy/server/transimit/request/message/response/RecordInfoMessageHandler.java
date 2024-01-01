package io.github.lunasaw.gbproxy.server.transimit.request.message.response;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.server.transimit.request.message.ServerMessageRequestProcessor;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gb28181.common.entity.response.DeviceRecord;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageServerHandlerAbstract;
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.entity.ToDevice;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class RecordInfoMessageHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "RecordInfo";

    private String             cmdType  = CMD_TYPE;

    public RecordInfoMessageHandler(MessageProcessorServer messageProcessorServer, SipUserGenerateServer sipUserGenerate) {
        super(messageProcessorServer, sipUserGenerate);
    }

    @Override
    public String getRootType() {
        return ServerMessageRequestProcessor.METHOD + RESPONSE;
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();

        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(userId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return;
        }

        DeviceRecord deviceRecord = parseXml(DeviceRecord.class);

        messageProcessorServer.updateDeviceRecord(userId, deviceRecord);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
