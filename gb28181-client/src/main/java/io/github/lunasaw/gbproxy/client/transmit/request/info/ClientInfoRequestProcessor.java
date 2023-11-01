package io.github.lunasaw.gbproxy.client.transmit.request.info;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;

/**
 * @author luna
 * @date 2023/10/18
 */
@Component
@Getter
@Setter
public class ClientInfoRequestProcessor extends SipRequestProcessorAbstract {


    public static final String METHOD = "INFO";

    private String method = METHOD;

    /**
     * 收到ACK请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {

    }

}
