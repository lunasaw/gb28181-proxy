package io.github.lunasaw.sip.common.transmit.event.request;

import javax.sip.RequestEvent;

/**
 * 对SIP事件进行处理，包括request， response， timeout， ioException, transactionTerminated,dialogTerminated
 * 
 * @author luna
 */
public interface SipRequestProcessor {

    /**
     * 对SIP事件进行处理
     * 
     * @param event SIP事件
     */
    void process(RequestEvent event);

}
