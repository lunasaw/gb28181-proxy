package io.github.lunasaw.gbproxy.server.config;

import java.util.Map;

import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageServerHandlerAbstract;
import io.github.lunasaw.gbproxy.server.transimit.request.message.ServerMessageRequestProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/16
 */
@Slf4j
@Component
@ComponentScan(basePackages = "io.github.lunasaw.gbproxy.server")
public class SipProxyServerAutoConfig implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        Map<String, MessageServerHandlerAbstract> messageHandlerMap = applicationContext.getBeansOfType(MessageServerHandlerAbstract.class);
        messageHandlerMap.forEach((k, v) -> ServerMessageRequestProcessor.addHandler(v));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
