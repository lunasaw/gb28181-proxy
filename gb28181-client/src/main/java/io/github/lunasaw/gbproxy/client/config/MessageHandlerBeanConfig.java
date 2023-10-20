package io.github.lunasaw.gbproxy.client.config;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.CatalogQueryMessageHandler;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.DeviceInfoQueryMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.Resource;

/**
 * @author luna
 * @date 2023/10/20
 */
@Configuration
@DependsOn("messageRequestProcessor")
public class MessageHandlerBeanConfig {

    @Resource
    private MessageProcessorClient messageProcessorClient;

    @Bean
    public CatalogQueryMessageHandler catalogQueryMessageHandler() {
        CatalogQueryMessageHandler messageHandler = new CatalogQueryMessageHandler();
        messageHandler.setMessageProcessorClient(messageProcessorClient);
        return messageHandler;
    }

    @Bean
    public DeviceInfoQueryMessageHandler deviceInfoQueryMessageHandler() {
        DeviceInfoQueryMessageHandler messageHandler = new DeviceInfoQueryMessageHandler();
        messageHandler.setMessageProcessorClient(messageProcessorClient);
        return messageHandler;
    }

}
