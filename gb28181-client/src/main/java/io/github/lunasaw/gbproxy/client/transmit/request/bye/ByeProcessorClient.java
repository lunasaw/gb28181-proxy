package io.github.lunasaw.gbproxy.client.transmit.request.bye;

/**
 * @author luna
 * @date 2023/11/14
 */
public interface ByeProcessorClient {

    void closeStream(String callId);
}
