package io.github.lunasaw.gbproxy.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorClient;
import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterResponseProcessor;
import io.github.lunasaw.gbproxy.server.transimit.cmd.response.invite.InviteResponseProcessor;
import io.github.lunasaw.sip.common.conf.SipProxyClientAutoConfig;

/**
 * @author luna
 * @date 2023/10/17
 */
@Configuration
public class SipProcessorConfiguration {

    @Autowired
    private RegisterProcessorClient registerProcessorClient;

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
    public SipProxyClientAutoConfig sipProxyClientAutoConfig() {
        return new SipProxyClientAutoConfig();
    }
}
