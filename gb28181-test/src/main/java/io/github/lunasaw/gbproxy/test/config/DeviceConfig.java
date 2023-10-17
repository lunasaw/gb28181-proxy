package io.github.lunasaw.gbproxy.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

/**
 * @author luna
 * @date 2023/10/17
 */
@Configuration
public class DeviceConfig {

    public static final String LOCAL_IP = "172.19.128.100";

    public static final String REMOTE_IP = "10.37.5.132";

    @Bean
    public FromDevice fromDevice() {
        return FromDevice.getInstance("33010602011187000001", LOCAL_IP, 8118);
    }

    @Bean
    public ToDevice toDevice() {
        return ToDevice.getInstance("41010500002000000010", REMOTE_IP, 8117);
    }

}
