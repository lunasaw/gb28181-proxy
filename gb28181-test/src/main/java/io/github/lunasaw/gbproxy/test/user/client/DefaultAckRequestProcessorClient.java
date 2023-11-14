package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.gbproxy.client.transmit.request.ack.AckRequestProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/11/7
 */
@Component
public class DefaultAckRequestProcessorClient implements AckRequestProcessorClient {


    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;


    @Override
    public Device getToDevice(String userId) {
        return DefaultRegisterProcessorClient.deviceMap.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
