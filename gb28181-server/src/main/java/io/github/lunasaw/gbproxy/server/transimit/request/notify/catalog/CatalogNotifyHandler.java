package io.github.lunasaw.gbproxy.server.transimit.request.notify.catalog;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceOtherUpdateNotify;
import io.github.lunasaw.gbproxy.server.transimit.request.notify.NotifyServerHandlerAbstract;
import io.github.lunasaw.gbproxy.server.transimit.request.notify.ServerNotifyRequestProcessor;
import io.github.lunasaw.sip.common.entity.DeviceSession;
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
public class CatalogNotifyHandler extends NotifyServerHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);

        String userId = deviceSession.getUserId();
        ToDevice toDevice = (ToDevice)sipUserGenerate.getToDevice(userId);
        if (toDevice == null) {
            // 未注册的设备不做处理
            return;
        }

        DeviceOtherUpdateNotify deviceOtherUpdateNotify = parseXml(DeviceOtherUpdateNotify.class);

        notifyProcessorServer.deviceNotifyUpdate(userId, deviceOtherUpdateNotify);
    }

    @Override
    public String getCmdType() {
        return CMD_TYPE;
    }

    @Override
    public String getRootType() {
        return ServerNotifyRequestProcessor.METHOD + RESPONSE;
    }
}
