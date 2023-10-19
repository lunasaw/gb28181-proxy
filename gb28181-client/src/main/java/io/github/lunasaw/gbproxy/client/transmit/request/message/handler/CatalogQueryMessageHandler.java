package io.github.lunasaw.gbproxy.client.transmit.request.message.handler;

import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageHandlerAbstract;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.sip.RequestEvent;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Data
@Slf4j
public class CatalogQueryMessageHandler extends MessageHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String cmdType = CMD_TYPE;


    @Override
    public void handForEvt(RequestEvent event) {
        log.info("handForEvt::event = {}", event);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
