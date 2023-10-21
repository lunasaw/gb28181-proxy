package io.github.lunasaw.gbproxy.server.transimit.request.message.notify;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.sip.common.entity.query.DeviceQuery;
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
public class KeepaliveNotifyMessageHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String             cmdType  = CMD_TYPE;

    public KeepaliveNotifyMessageHandler(MessageProcessorServer messageProcessorServer) {
        super(messageProcessorServer);
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();
        String sipId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)messageProcessorServer.getFromDevice(userId);
        ToDevice toDevice = (ToDevice)messageProcessorServer.getToDevice(sipId);

        DeviceKeepLiveNotify deviceKeepLiveNotify = parseRequest(event, fromDevice.getCharset(), DeviceKeepLiveNotify.class);

        messageProcessorServer.keepLiveDevice(deviceKeepLiveNotify);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
