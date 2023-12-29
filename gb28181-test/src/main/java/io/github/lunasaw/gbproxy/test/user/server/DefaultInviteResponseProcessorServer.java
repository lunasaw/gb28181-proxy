package io.github.lunasaw.gbproxy.test.user.server;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.transimit.response.invite.InviteResponseProcessorServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/21
 */
@Component
@Slf4j
public class DefaultInviteResponseProcessorServer implements InviteResponseProcessorServer {

    @Override
    public void responseTrying() {
        log.info("responseTrying::");
    }
}
