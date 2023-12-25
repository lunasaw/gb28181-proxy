package io.github.lunasaw.gbproxy.server.transimit.request.bye;

import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/12/25
 */
public interface ByeProcessorServer extends SipUserGenerate {

    void receiveBye(String userId);
}
