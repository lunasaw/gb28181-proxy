package io.github.lunasaw.gbproxy.client.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;

/**
 * @author luna
 * @date 2023/10/20
 */
@Configuration
@ComponentScan(basePackages = "io.github.lunasaw.gbproxy.client")
public class MessageHandlerBeanConfig {

    @Resource
    private MessageProcessorClient messageProcessorClient;

    // @Bean
    // public CatalogQueryMessageHandler catalogQueryMessageHandler() {
    // CatalogQueryMessageHandler messageHandler = new CatalogQueryMessageHandler();
    // messageHandler.setMessageProcessorClient(messageProcessorClient);
    // return messageHandler;
    // }

// @Bean
// public DeviceInfoQueryMessageHandler deviceInfoQueryMessageHandler() {
// DeviceInfoQueryMessageHandler messageHandler = new DeviceInfoQueryMessageHandler();
// messageHandler.setMessageProcessorClient(messageProcessorClient);
// return messageHandler;
// }

}
