package io.github.lunasaw.gbproxy.client.transmit.request.bye;

import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/11/14
 */
public interface ByeProcessorClient extends SipUserGenerate {

    void closeStream(String callId);
}
