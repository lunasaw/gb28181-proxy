package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.gbproxy.client.transmit.request.info.InfoProcessorClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.entity.Device;
/**
 * @author luna
 * @date 2023/11/7
 */
@Component
@Slf4j
public class DefaultInfoProcessorClient implements InfoProcessorClient {


    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Override
    public void receiveInfo(String userId, String content) {
        log.info("receiveInfo userId = {} ::content = {}", userId, content);
    }
}
