package io.github.lunasaw.gbproxy.common.transmit.event.timeout;

import javax.sip.TimeoutEvent;

public interface ITimeoutProcessor {
    void process(TimeoutEvent event);
}
