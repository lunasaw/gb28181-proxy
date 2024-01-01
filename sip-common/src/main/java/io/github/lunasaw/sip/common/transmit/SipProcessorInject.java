package io.github.lunasaw.sip.common.transmit;

import java.util.EventObject;

/**
 * @author luna
 * @date 2023/12/29
 */
public interface SipProcessorInject {

    void before(EventObject eventObject);

    void after();
}
