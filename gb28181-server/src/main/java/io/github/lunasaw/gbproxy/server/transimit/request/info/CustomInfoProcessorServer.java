package io.github.lunasaw.gbproxy.server.transimit.request.info;

import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(InfoProcessorServer.class)
public class CustomInfoProcessorServer implements InfoProcessorServer {
    @Override
    public void dealInfo(String userId, String content) {

    }
}
