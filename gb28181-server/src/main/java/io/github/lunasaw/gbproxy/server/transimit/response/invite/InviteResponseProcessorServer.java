package io.github.lunasaw.gbproxy.server.transimit.response.invite;

import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/10/21
 */
public interface InviteResponseProcessorServer extends SipUserGenerate {
    void responseTrying();

}
