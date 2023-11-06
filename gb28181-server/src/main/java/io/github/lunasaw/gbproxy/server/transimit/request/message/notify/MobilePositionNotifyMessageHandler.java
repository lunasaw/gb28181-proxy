package io.github.lunasaw.gbproxy.server.transimit.request.message.notify;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.notify.MobilePositionNotify;
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
public class MobilePositionNotifyMessageHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "MobilePosition";

    private String             cmdType  = CMD_TYPE;

    public MobilePositionNotifyMessageHandler(MessageProcessorServer messageProcessorServer) {
        super(messageProcessorServer);
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
        FromDevice fromDevice = (FromDevice)messageProcessorServer.getFromDevice();
        ToDevice toDevice = (ToDevice) messageProcessorServer.getToDevice(userId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return;
        }

        MobilePositionNotify mobilePositionNotify = parseRequest(event, fromDevice.getCharset(), MobilePositionNotify.class);

        messageProcessorServer.updateMobilePosition(mobilePositionNotify);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
