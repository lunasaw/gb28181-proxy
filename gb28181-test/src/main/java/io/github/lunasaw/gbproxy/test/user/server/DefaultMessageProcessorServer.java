package io.github.lunasaw.gbproxy.test.user.server;

import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import io.github.lunasaw.sip.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.sip.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.sip.common.entity.notify.MediaStatusNotify;
import io.github.lunasaw.sip.common.entity.notify.MobilePositionNotify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.query.DeviceRecordQuery;
import io.github.lunasaw.sip.common.entity.response.DeviceInfo;
import io.github.lunasaw.sip.common.entity.response.DeviceRecord;
import io.github.lunasaw.sip.common.entity.response.DeviceResponse;
import io.github.lunasaw.sip.common.entity.response.DeviceStatus;
import io.github.lunasaw.sip.common.utils.XmlUtils;

/**
 * @author luna
 * @date 2023/10/17
 */
@Slf4j
@Component
public class DefaultMessageProcessorServer implements MessageProcessorServer {

    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;
    @Autowired
    @Qualifier("serverTo")
    private Device toDevice;

    @Override
    public Device getToDevice(String userId) {
        return toDevice;
    }

    @Override
    public Device getFromDevice(String userId) {
        return fromDevice;
    }

    @Override
    public void keepLiveDevice(DeviceKeepLiveNotify deviceKeepLiveNotify) {
        log.info("接收到设备的心跳 keepLiveDevice::deviceKeepLiveNotify = {}", deviceKeepLiveNotify);
    }

    @Override
    public void updateRemoteAddress(RemoteAddressInfo remoteAddressInfo) {
        log.info("接收到设备的地址信息 updateRemoteAddress::remoteAddressInfo = {}", remoteAddressInfo);
    }

    @Override
    public void updateDeviceAlarm(DeviceAlarmNotify deviceAlarmNotify) {
        log.info("接收到设备的告警信息 updateDeviceAlarm::deviceAlarmNotify = {}", deviceAlarmNotify);
    }

    @Override
    public void updateMobilePosition(MobilePositionNotify mobilePositionNotify) {
        log.info("接收到设备的位置信息 updateMobilePosition::mobilePositionNotify = {}", mobilePositionNotify);
    }

    @Override
    public void updateMediaStatus(MediaStatusNotify mediaStatusNotify) {
        log.info("接收到设备的媒体状态信息 updateMediaStatus::mediaStatusNotify = {}", mediaStatusNotify);
    }
}
