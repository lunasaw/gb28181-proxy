package io.github.lunasaw.sip.common.transmit.event.request;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.transmit.event.SipMethod;

/**
 * 对SIP事件进行处理，包括request， response， timeout， ioException, transactionTerminated,dialogTerminated
 *
 * @author luna
 */
public interface SipRequestProcessor {

    /**
     * 对SIP事件进行处理
     *
     * @param event SIP事件
     */
    void process(RequestEvent event);

    /**
     * 获取处理器支持的SIP方法
     * 优先从注解获取，如果没有注解则返回null
     *
     * @return SIP方法名称，如果未配置则返回null
     */
    default String getSupportedMethod() {
        SipMethod sipMethod = this.getClass().getAnnotation(SipMethod.class);
        return sipMethod != null ? sipMethod.value() : null;
    }

    /**
     * 获取处理器优先级
     * 优先从注解获取，如果没有注解则返回默认值100
     *
     * @return 优先级，数值越小优先级越高
     */
    default int getPriority() {
        SipMethod sipMethod = this.getClass().getAnnotation(SipMethod.class);
        return sipMethod != null ? sipMethod.priority() : 100;
    }
}
