package io.github.lunasaw.sip.common.conf;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.CustomSipProcessorInject;
import io.github.lunasaw.sip.common.transmit.SipProcessorInject;
import io.github.lunasaw.sip.common.transmit.SipProcessorObserver;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/16
 */
@Component
@Slf4j
@ComponentScan("io.github.lunasaw.sip.common")
@Configuration
public class SipProxyAutoConfig implements InitializingBean, ApplicationContextAware {

    private static final String METHOD = "method";

    private ApplicationContext  applicationContext;

    @Override
    public void afterPropertiesSet() {
        // 获取所有的SipResponseProcessorAbstract bean
        Map<String, SipResponseProcessor> responseProcessorAbstractMap =
            applicationContext.getBeansOfType(SipResponseProcessor.class);

        responseProcessorAbstractMap.forEach((k, v) -> {
            try {
                if (v instanceof SipResponseProcessorAbstract) {
                    Field field = v.getClass().getDeclaredField(METHOD);
                    field.setAccessible(true);
                    String method = field.get(v).toString();
                    SipProcessorObserver.addResponseProcessor(method, v);
                }
            } catch (Exception e) {
                log.error("afterPropertiesSet:: bean = {}", k);
            }
        });

        Map<String, SipRequestProcessor> requestProcessorAbstractMap = applicationContext.getBeansOfType(SipRequestProcessor.class);

        requestProcessorAbstractMap.forEach((k, v) -> {
            try {
                if (v instanceof SipRequestProcessorAbstract) {
                    Field field = v.getClass().getDeclaredField(METHOD);
                    field.setAccessible(true);
                    String method = field.get(v).toString();
                    SipProcessorObserver.addRequestProcessor(method, v);
                }
            } catch (Exception e) {
                log.error("afterPropertiesSet:: bean = {}", k);
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public SipProcessorInject sipProcessorInject() {
        return new CustomSipProcessorInject();
    }
}
