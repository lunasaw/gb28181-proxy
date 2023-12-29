package io.github.lunasaw.gbproxy.client.user;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import io.github.lunasaw.sip.common.entity.Device;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(SipUserGenerateClient.class)
public class CustomSipUserGenerateClient implements SipUserGenerateClient {
    @Override
    public Device getToDevice(String userId) {
        return null;
    }

    @Override
    public Device getFromDevice() {
        return null;
    }
}
