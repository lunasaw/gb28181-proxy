package io.github.lunasaw.gbproxy.client.transmit.response.bye;

import javax.sip.ResponseEvent;

import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import lombok.Data;

/**
 * BYE请求响应器
 *
 * @author weidian
 */
@Component
@Data
public class ByeResponseProcessor extends SipResponseProcessorAbstract {

    public static final String METHOD = "BYE";

    private String method = METHOD;

    /**
     * 处理BYE响应
     *
     * @param evt
     */
    @Override
    public void process(ResponseEvent evt) {
        // Auto-generated method stub
    }

}
