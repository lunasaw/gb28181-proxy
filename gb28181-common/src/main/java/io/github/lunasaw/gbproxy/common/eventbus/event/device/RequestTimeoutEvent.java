package io.github.lunasaw.gbproxy.common.eventbus.event.device;

import javax.sip.TimeoutEvent;

import org.springframework.context.ApplicationEvent;

/**
 * @author lin
 */
public class RequestTimeoutEvent extends ApplicationEvent {
    public RequestTimeoutEvent(Object source) {
        super(source);
    }


    private TimeoutEvent timeoutEvent;

    public TimeoutEvent getTimeoutEvent() {
        return timeoutEvent;
    }

    public void setTimeoutEvent(TimeoutEvent timeoutEvent) {
        this.timeoutEvent = timeoutEvent;
    }
}
