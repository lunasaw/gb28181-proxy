package io.github.lunasaw.gbproxy.test.user.server;

import io.github.lunasaw.gbproxy.server.transimit.request.bye.ByeProcessorServer;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/25
 */
@Component
@Slf4j
public class DefaultByeProcessorServer implements ByeProcessorServer {
    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;


    @Override
    public void receiveBye(String userId) {
        log.info("receiveBye::userId = {}", userId);
    }
}
