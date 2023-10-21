package io.github.lunasaw.gbproxy.test.user;

import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.sip.common.entity.notify.DeviceKeepLiveNotify;
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
}
