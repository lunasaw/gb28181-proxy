package io.github.lunasaw.sip.common.transmit.event.timeout;

import javax.sip.TimeoutEvent;

public interface ITimeoutProcessor {
    void process(TimeoutEvent event);
}
