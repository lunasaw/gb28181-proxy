package io.github.lunasaw.gbproxy.server.transimit.request.bye;

import io.github.lunasaw.sip.common.entity.Device;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author weidian
 */
@Component
@Slf4j
@ConditionalOnMissingBean(ByeProcessorServer.class)
public class CustomByeProcessorServer implements ByeProcessorServer {

    @Override
    public void receiveBye(String userId) {

    }
}