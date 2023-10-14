package io.github.lunasaw.sipproxy.common.eventbus.event;

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
