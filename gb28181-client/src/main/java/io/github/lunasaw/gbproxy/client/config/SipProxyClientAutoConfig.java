package io.github.lunasaw.gbproxy.client.config;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/16
 */
@Slf4j
@Component
public class SipProxyClientAutoConfig implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, MessageHandler> messageHandlerMap = applicationContext.getBeansOfType(MessageHandler.class);
        messageHandlerMap.forEach((k, v) -> MessageRequestProcessor.addHandler(v));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
