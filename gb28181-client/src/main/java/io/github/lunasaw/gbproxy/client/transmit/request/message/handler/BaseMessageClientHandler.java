package io.github.lunasaw.gbproxy.client.transmit.request.message.handler;

import javax.sip.RequestEvent;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class BaseMessageClientHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String cmdType = CMD_TYPE;

    private MessageProcessorClient messageProcessorClient;

    public BaseMessageClientHandler(MessageProcessorClient messageProcessorClient) {
        super(messageProcessorClient);
    }

    @Override
    public void handForEvt(RequestEvent event) {
        log.info("handForEvt::event = {}", event);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
