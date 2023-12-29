package io.github.lunasaw.gbproxy.test.user.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import io.github.lunasaw.gbproxy.server.transimit.request.info.InfoProcessorServer;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/26
 */
@Component
public class DefaultInfoProcessorServer implements InfoProcessorServer {
    @Autowired
    @Qualifier("serverFrom")
    private Device fromDevice;

    @Override
    public void dealInfo(String userId, String content) {

    }
}
