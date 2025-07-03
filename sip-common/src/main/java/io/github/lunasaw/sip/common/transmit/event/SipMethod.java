package io.github.lunasaw.sip.common.transmit.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SIP方法注解，用于标记处理器支持的SIP方法类型
 * 替代反射获取method字段的方式，提升性能和安全性
 *
 * @author luna
 * @date 2024/01/01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SipMethod {

    /**
     * SIP方法名称
     * 如：MESSAGE, REGISTER, INVITE, BYE, ACK, CANCEL, INFO, SUBSCRIBE, NOTIFY
     *
     * @return SIP方法名称
     */
    String value();

    /**
     * 处理器优先级，数值越小优先级越高
     * 当有多个处理器处理同一种方法时，按优先级排序
     *
     * @return 优先级
     */
    int priority() default 100;
}