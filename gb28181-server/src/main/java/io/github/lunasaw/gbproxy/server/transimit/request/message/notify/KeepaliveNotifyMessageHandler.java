package io.github.lunasaw.gbproxy.server.transimit.request.message.notify;

import javax.sip.RequestEvent;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.entity.base.DeviceSession;
import io.github.lunasaw.sip.common.entity.notify.DeviceKeepLiveNotify;
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
public class KeepaliveNotifyMessageHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "Keepalive";

    private String             cmdType  = CMD_TYPE;

    public KeepaliveNotifyMessageHandler(MessageProcessorServer messageProcessorServer) {
        super(messageProcessorServer);
    }

    @Override
    public String getRootType() {
        return Notify;
    }


    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();
        String deviceId = deviceSession.getSipId();

        // 设备查询
        FromDevice fromDevice = (FromDevice)messageProcessorServer.getFromDevice();
        ToDevice toDevice = (ToDevice)messageProcessorServer.getToDevice(deviceId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return;
        }
        DeviceKeepLiveNotify deviceKeepLiveNotify = parseRequest(event, fromDevice.getCharset(), DeviceKeepLiveNotify.class);
        messageProcessorServer.keepLiveDevice(deviceKeepLiveNotify);

        RemoteAddressInfo remoteAddressInfo = SipUtils.getRemoteAddressFromRequest((SIPRequest)event.getRequest());
        messageProcessorServer.updateRemoteAddress(userId, remoteAddressInfo);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
