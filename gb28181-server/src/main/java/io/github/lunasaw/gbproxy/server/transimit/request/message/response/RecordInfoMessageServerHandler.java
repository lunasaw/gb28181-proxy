package io.github.lunasaw.gbproxy.server.transimit.request.message.response;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.response.DeviceRecord;
import io.github.lunasaw.sip.common.utils.SipUtils;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageServerHandlerAbstract;
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
public class RecordInfoMessageServerHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "RecordInfo";

    private String             cmdType  = CMD_TYPE;

    public RecordInfoMessageServerHandler(MessageProcessorServer messageProcessorServer) {
        super(messageProcessorServer);
    }

    @Override
    public String getRootType() {
        return RESPONSE;
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();

        ToDevice toDevice = (ToDevice)messageProcessorServer.getToDevice(userId);
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
