package io.github.lunasaw.gbproxy.client.transmit.request.cancel;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * SIP命令类型： CANCEL请求
 *
 * @author weidian
 */
@Component
@Getter
@Setter
@Slf4j
public class CancelRequestProcessor extends SipRequestProcessorAbstract {

    private final String METHOD = "CANCEL";

    private String method = METHOD;

    /**
     * 处理CANCEL请求
     *
     * @param evt 事件
     */
    @Override
    public void process(RequestEvent evt) {
        // TODO 优先级99 Cancel Request消息实现，此消息一般为级联消息，上级给下级发送请求取消指令
    }

}
