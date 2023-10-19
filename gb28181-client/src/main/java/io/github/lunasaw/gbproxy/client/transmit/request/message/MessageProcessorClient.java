package io.github.lunasaw.gbproxy.client.transmit.request.message;

import io.github.lunasaw.sip.common.entity.SipTransaction;
import io.github.lunasaw.sip.common.service.SipUserGenerate;

/**
 * @author luna
 * @date 2023/10/18
 */
public interface MessageProcessorClient extends SipUserGenerate {

    /**
     * 获取之前的事务
     *
     * @param callId 事物Id
     * @return
     */
    SipTransaction getTransaction(String callId);

}
