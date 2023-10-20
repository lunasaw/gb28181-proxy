package io.github.lunasaw.gbproxy.client.transmit.request.message.handler;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Data
@Slf4j
public class BaseMessageHandler extends MessageHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String cmdType = CMD_TYPE;

    private MessageProcessorClient messageProcessorClient;

    @Override
    public void handForEvt(RequestEvent event) {
        log.info("handForEvt::event = {}", event);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
