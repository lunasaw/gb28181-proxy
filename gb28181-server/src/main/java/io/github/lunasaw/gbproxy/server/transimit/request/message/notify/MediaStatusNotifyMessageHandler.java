package io.github.lunasaw.gbproxy.server.transimit.request.message.notify;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageServerHandlerAbstract;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.notify.MediaStatusNotify;
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
public class MediaStatusNotifyMessageHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "MediaStatus";

    private String             cmdType  = CMD_TYPE;

    public MediaStatusNotifyMessageHandler(MessageProcessorServer messageProcessorServer) {
        super(messageProcessorServer);
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();
        String deviceId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)messageProcessorServer.getFromDevice(userId);
        ToDevice toDevice = (ToDevice)messageProcessorServer.getToDevice(deviceId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return;
        }

        MediaStatusNotify mediaStatusNotify = parseRequest(event, fromDevice.getCharset(), MediaStatusNotify.class);

        messageProcessorServer.updateMediaStatus(mediaStatusNotify);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
