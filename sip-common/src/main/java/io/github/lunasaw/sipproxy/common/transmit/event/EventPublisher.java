package io.github.lunasaw.sipproxy.common.transmit.event;

import javax.sip.TimeoutEvent;

import org.springframework.stereotype.Component;

public interface EventPublisher {



    public void requestTimeOut(TimeoutEvent timeoutEvent);
}
