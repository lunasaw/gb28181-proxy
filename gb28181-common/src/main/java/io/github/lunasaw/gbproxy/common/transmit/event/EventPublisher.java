package io.github.lunasaw.gbproxy.common.transmit.event;

import io.github.lunasaw.gbproxy.common.eventbus.event.device.RequestTimeoutEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.sip.TimeoutEvent;

@Component
public class EventPublisher {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;


    public void requestTimeOut(TimeoutEvent timeoutEvent) {
        RequestTimeoutEvent requestTimeoutEvent = new RequestTimeoutEvent(this);
        requestTimeoutEvent.setTimeoutEvent(timeoutEvent);
        applicationEventPublisher.publishEvent(requestTimeoutEvent);
    }
}
