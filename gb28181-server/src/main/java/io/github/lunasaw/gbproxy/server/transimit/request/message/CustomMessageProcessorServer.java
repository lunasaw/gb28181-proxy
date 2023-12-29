package io.github.lunasaw.gbproxy.server.transimit.request.message;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MediaStatusNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MobilePositionNotify;
import io.github.lunasaw.gb28181.common.entity.response.DeviceRecord;
import io.github.lunasaw.gb28181.common.entity.response.DeviceResponse;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
public class CustomMessageProcessorServer implements MessageProcessorServer {
    @Override
    public void keepLiveDevice(DeviceKeepLiveNotify deviceKeepLiveNotify) {

    }

    @Override
    public void updateRemoteAddress(String userId, RemoteAddressInfo remoteAddressInfo) {

    }

    @Override
    public void updateDeviceAlarm(DeviceAlarmNotify deviceAlarmNotify) {

    }

    @Override
    public void updateMobilePosition(MobilePositionNotify mobilePositionNotify) {

    }

    @Override
    public void updateMediaStatus(MediaStatusNotify mediaStatusNotify) {

    }

    @Override
    public void updateDeviceRecord(String userId, DeviceRecord deviceRecord) {

    }

    @Override
    public void updateDeviceResponse(String userId, DeviceResponse deviceResponse) {

    }
}
