package io.github.lunasaw.gbproxy.client.transmit.request.info;

import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/11/7
 */
public interface InfoProcessorClient {

    void receiveInfo(String userId, String content);

}
