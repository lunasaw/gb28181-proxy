package io.github.lunasaw.gbproxy.test.user;

import io.github.lunasaw.sip.common.entity.response.DeviceInfo;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.entity.Device;

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
        return null;
    }
}
