package io.github.lunasaw.gbproxy.server.transimit.response.invite;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(InviteResponseProcessorServer.class)
public class CustomInviteResponseProcessorServer implements InviteResponseProcessorServer {
    @Override
    public void responseTrying() {

    }
}
