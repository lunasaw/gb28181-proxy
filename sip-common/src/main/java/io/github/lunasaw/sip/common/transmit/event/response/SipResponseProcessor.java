package io.github.lunasaw.sip.common.transmit.event.response;

import javax.sip.ResponseEvent;

import io.github.lunasaw.sip.common.transmit.event.SipMethod;

/**
 * 处理接收IPCamera发来的SIP协议响应消息
 * 
 * @author swwheihei
 */
public interface SipResponseProcessor {

	/**
	 * 处理接收IPCamera发来的SIP协议响应消息
	 * @param evt 消息对象
	 */
	void process(ResponseEvent evt);

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
