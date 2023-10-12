package io.github.lunasaw.gbproxy.common.transmit.event;

import org.springframework.stereotype.Component;

import javax.sip.TimeoutEvent;

@Component
public class EventPublisher {

    public void requestTimeOut(TimeoutEvent timeoutEvent) {
        RequestTimeoutEvent requestTimeoutEvent = new RequestTimeoutEvent(this);
        requestTimeoutEvent.setTimeoutEvent(timeoutEvent);
        applicationEventPublisher.publishEvent(requestTimeoutEvent);
    }
}
