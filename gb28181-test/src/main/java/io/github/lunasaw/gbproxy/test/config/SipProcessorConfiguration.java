package io.github.lunasaw.gbproxy.test.config;

import io.github.lunasaw.gbproxy.client.config.MessageClientHandlerBeanConfig;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageRequestProcessor;
import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterProcessorServer;
import io.github.lunasaw.gbproxy.server.transimit.request.register.RegisterRequestProcessor;
import io.github.lunasaw.gbproxy.test.user.DefaultMessageProcessorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorClient;
import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterResponseProcessor;
import io.github.lunasaw.gbproxy.server.transimit.cmd.response.invite.InviteResponseProcessor;
import io.github.lunasaw.sip.common.conf.SipProxyAutoConfig;
import org.springframework.context.annotation.Import;

/**
 * @author luna
 * @date 2023/10/17
 */
@Configuration
@Import(value = MessageClientHandlerBeanConfig.class)
public class SipProcessorConfiguration {

    @Autowired
    private RegisterProcessorClient registerProcessorClient;

    @Autowired
    private RegisterProcessorServer registerProcessorServer;

    @Autowired
    private DefaultMessageProcessorClient defaultMessageProcessorClient;

    @Bean
    public RegisterResponseProcessor registerResponseProcessor() {
        RegisterResponseProcessor registerResponseProcessor = new RegisterResponseProcessor();
        registerResponseProcessor.setRegisterProcessorClient(registerProcessorClient);
        return registerResponseProcessor;
    }

    @Bean
    public InviteResponseProcessor inviteResponseProcessor() {
        return new InviteResponseProcessor(registerProcessorClient);
    }

    @Bean
    public RegisterRequestProcessor registerRequestProcessor() {
        RegisterRequestProcessor registerRequestProcessor = new RegisterRequestProcessor();
        registerRequestProcessor.setRegisterProcessorServer(registerProcessorServer);
        return registerRequestProcessor;
    }


    // message---------

    // @Bean
    public MessageRequestProcessor messageRequestProcessor() {
        MessageRequestProcessor messageRequestProcessor = new MessageRequestProcessor();
        messageRequestProcessor.setMessageProcessorClient(defaultMessageProcessorClient);
        return messageRequestProcessor;
    }

    @Bean
    public SipProxyAutoConfig sipProxyAutoConfig() {
        return new SipProxyAutoConfig();
    }
}
