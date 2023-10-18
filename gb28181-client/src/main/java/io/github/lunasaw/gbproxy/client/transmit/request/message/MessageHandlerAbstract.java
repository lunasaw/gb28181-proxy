package io.github.lunasaw.gbproxy.client.transmit.request.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

/**
 * @author weidian
 */
@Component
public abstract class MessageHandlerAbstract implements MessageHandler {

    public Map<String, MessageHandler> messageHandlerMap = new ConcurrentHashMap<>();

    public void addHandler(MessageHandler messageHandler) {
        messageHandlerMap.put(messageHandler.getCmdType(), messageHandler);
    }


}
