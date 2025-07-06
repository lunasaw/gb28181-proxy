package io.github.lunasaw.sip.common.conf;

import java.lang.reflect.Field;
import java.util.Map;

import io.github.lunasaw.sip.common.transmit.CustomerSipListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import lombok.extern.slf4j.Slf4j;

import javax.sip.SipListener;

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

    @Bean
    @ConditionalOnMissingBean
    public SipListener sipListener() {
        // 默认使用同步监听器，可以通过配置切换为异步监听器
        return CustomerSipListener.getInstance();
    }

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
                    CustomerSipListener.getInstance().addResponseProcessor(method, v);
                    log.debug("注册响应处理器: {} -> {}", method, v.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.error("注册响应处理器失败: bean = {}", k, e);
            }
        });

        Map<String, SipRequestProcessor> requestProcessorAbstractMap = applicationContext.getBeansOfType(SipRequestProcessor.class);

        requestProcessorAbstractMap.forEach((k, v) -> {
            try {
                if (v instanceof SipRequestProcessorAbstract) {
                    Field field = v.getClass().getDeclaredField(METHOD);
                    field.setAccessible(true);
                    String method = field.get(v).toString();
                    CustomerSipListener.getInstance().addRequestProcessor(method, v);
                    log.debug("注册请求处理器: {} -> {}", method, v.getClass().getSimpleName());
                }
            } catch (Exception e) {
                log.error("注册请求处理器失败: bean = {}", k, e);
            }
        });

        log.info("SIP处理器注册完成 - 响应处理器: {}, 请求处理器: {}",
                responseProcessorAbstractMap.size(), requestProcessorAbstractMap.size());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
