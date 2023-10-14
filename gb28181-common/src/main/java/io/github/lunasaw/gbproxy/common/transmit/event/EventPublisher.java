package io.github.lunasaw.gbproxy.common.transmit.event;

import javax.sip.TimeoutEvent;

import org.springframework.stereotype.Component;

public interface EventPublisher {



    public void requestTimeOut(TimeoutEvent timeoutEvent);
}
