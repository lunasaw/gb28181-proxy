package io.github.lunasaw.gbproxy.test.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.response.DeviceInfo;
import io.github.lunasaw.sip.common.entity.response.DeviceItem;
import io.github.lunasaw.sip.common.entity.response.DeviceResponse;
import io.github.lunasaw.sip.common.utils.XmlUtils;

/**
 * @author luna
 * @date 2023/10/17
 */
@Component
public class DefaultMessageProcessorClient implements MessageProcessorClient {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;
    @Autowired
    @Qualifier("clientTo")
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
    public DeviceInfo getDeviceInfo(String userId) {
        return (DeviceInfo)XmlUtils.parseFile("classpath:device/deviceInfo.xml", DeviceInfo.class);
    }

    @Override
    public List<DeviceItem> getDeviceItem(String userId) {
        DeviceResponse response = (DeviceResponse)XmlUtils.parseFile("classpath:device/catalog.xml", DeviceResponse.class);
        return response.getDeviceItemList();
    }
}
