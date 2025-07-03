package io.github.lunasaw.sip.common.conf;

import java.util.Comparator;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.github.lunasaw.sip.common.async.SipAsyncConfig;
import io.github.lunasaw.sip.common.config.SipConfigValidator;
import io.github.lunasaw.sip.common.pool.SipPoolConfig;
import io.github.lunasaw.sip.common.transmit.CustomSipProcessorInject;
import io.github.lunasaw.sip.common.transmit.SipProcessorInject;
import io.github.lunasaw.sip.common.transmit.SipProcessorObserver;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP代理自动配置类
 * 重构后移除反射依赖，使用注解驱动的方式注册处理器
 * 集成配置验证和管理功能
 *
 * @author luna
 * @date 2023/10/16
 */
@Component
@Slf4j
@ComponentScan("io.github.lunasaw.sip.common")
@Configuration
@EnableConfigurationProperties({SipAsyncConfig.class, SipPoolConfig.class})
public class SipProxyAutoConfig implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private SipConfigValidator sipConfigValidator;

    @Override
    public void afterPropertiesSet() {
        try {
            // 首先验证配置
            validateConfigurations();

            // 然后注册处理器
            registerResponseProcessors();
            registerRequestProcessors();

            // 输出配置摘要
            logConfigurationSummary();

        } catch (Exception e) {
            log.error("SIP自动配置初始化失败", e);
            throw new RuntimeException("SIP自动配置初始化失败", e);
        }
    }

    /**
     * 验证配置参数
     */
    private void validateConfigurations() {
        try {
            sipConfigValidator.validateAllConfigs();
            log.info("SIP配置验证通过");
        } catch (Exception e) {
            log.error("SIP配置验证失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 注册SIP响应处理器
     */
    private void registerResponseProcessors() {
        Map<String, SipResponseProcessor> responseProcessorMap =
            applicationContext.getBeansOfType(SipResponseProcessor.class);

        int registeredCount = 0;
        for (SipResponseProcessor processor : responseProcessorMap.values()) {
            if (StringUtils.hasText(processor.getSupportedMethod())) {
                try {
                    String method = processor.getSupportedMethod();
                    SipProcessorObserver.addResponseProcessor(method, processor);
                    log.info("注册SIP响应处理器: {} -> {}", method, processor.getClass().getSimpleName());
                    registeredCount++;
                } catch (Exception e) {
                    log.error("注册SIP响应处理器失败: {}", processor.getClass().getSimpleName(), e);
                }
            } else {
                log.warn("跳过未配置SIP方法的响应处理器: {}", processor.getClass().getSimpleName());
            }
        }

        log.info("完成SIP响应处理器注册，共注册 {} 个处理器", registeredCount);
    }

    /**
     * 注册SIP请求处理器
     */
    private void registerRequestProcessors() {
        Map<String, SipRequestProcessor> requestProcessorMap =
            applicationContext.getBeansOfType(SipRequestProcessor.class);

        int registeredCount = 0;
        for (SipRequestProcessor processor : requestProcessorMap.values()) {
            if (StringUtils.hasText(processor.getSupportedMethod())) {
                try {
                    String method = processor.getSupportedMethod();
                    SipProcessorObserver.addRequestProcessor(method, processor);
                    log.info("注册SIP请求处理器: {} -> {} (优先级: {})",
                        method, processor.getClass().getSimpleName(), processor.getPriority());
                    registeredCount++;
                } catch (Exception e) {
                    log.error("注册SIP请求处理器失败: {}", processor.getClass().getSimpleName(), e);
                }
            } else {
                log.warn("跳过未配置SIP方法的请求处理器: {}", processor.getClass().getSimpleName());
            }
        }

        // 按优先级排序处理器
        requestProcessorMap.values().stream()
            .filter(processor -> StringUtils.hasText(processor.getSupportedMethod()))
            .sorted(Comparator.comparingInt(SipRequestProcessor::getPriority))
            .forEach(processor -> {
                log.debug("处理器优先级: {} - {} ({})",
                    processor.getPriority(), processor.getClass().getSimpleName(), processor.getSupportedMethod());
            });

        log.info("完成SIP请求处理器注册，共注册 {} 个处理器", registeredCount);
    }

    /**
     * 输出配置摘要
     */
    private void logConfigurationSummary() {
        String summary = sipConfigValidator.getConfigSummary();
        log.info("SIP框架初始化完成:\n{}", summary);
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

    /**
     * 创建配置验证器Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public SipConfigValidator sipConfigValidator(SipAsyncConfig asyncConfig, SipPoolConfig poolConfig) {
        return new SipConfigValidator(asyncConfig, poolConfig);
    }
}
