package io.github.lunasaw.gbproxy.common.transmit.event;

public interface Event {
    /**
     * 回调
     * 
     * @param eventResult
     */
    void response(EventResult eventResult);
}