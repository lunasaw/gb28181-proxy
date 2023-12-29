package io.github.lunasaw.gbproxy.client.transmit.request.bye;

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
}
