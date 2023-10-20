package io.github.lunasaw.gbproxy.test.user;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.google.common.base.Joiner;

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
        // 获取文件解析转为模型返回
        return new DeviceInfo();
    }

    @Override
    public List<DeviceItem> getDeviceItem(String userId) {

        try {
            File file = ResourceUtils.getFile("classpath:device/catalog.xml");
            List<String> strings = Files.readAllLines(Paths.get(file.getAbsolutePath()));

            String join = Joiner.on("\n").join(strings);
            DeviceResponse response = (DeviceResponse)XmlUtils.parseObj(join, DeviceResponse.class);

            return response.getDeviceItemList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
