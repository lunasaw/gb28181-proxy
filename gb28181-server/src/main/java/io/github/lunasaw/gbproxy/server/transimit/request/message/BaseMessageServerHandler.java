package io.github.lunasaw.gbproxy.server.transimit.request.message;

import javax.sip.RequestEvent;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class BaseMessageServerHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = "Catalog";

    private String             cmdType  = CMD_TYPE;

    public BaseMessageServerHandler(MessageProcessorServer messageProcessorServer) {
        super(messageProcessorServer);
    }

    @Override
    public void handForEvt(RequestEvent event) {
        log.info("handForEvt::event = {}", event);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }

    @Override
    public String getRootType() {
        return super.getRootType();
    }
}
