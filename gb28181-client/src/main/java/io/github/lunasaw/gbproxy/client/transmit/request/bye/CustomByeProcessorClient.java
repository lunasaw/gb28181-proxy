package io.github.lunasaw.gbproxy.client.transmit.request.bye;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(ByeProcessorClient.class)
public class CustomByeProcessorClient implements ByeProcessorClient {
    @Override
    public void closeStream(String callId) {

    }

    @Override
    public Device getToDevice(String userId) {
        return null;
    }

    @Override
    public Device getFromDevice() {
        return null;
    }
}
