package io.github.lunasaw.gbproxy.client.transmit.request.info;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(InfoProcessorClient.class)
public class CustomInfoProcessorClient implements InfoProcessorClient {
    @Override
    public void receiveInfo(String userId, String content) {

    }
}
