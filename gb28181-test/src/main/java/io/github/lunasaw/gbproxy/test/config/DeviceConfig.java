package io.github.lunasaw.gbproxy.test.config;

import com.luna.common.net.IPAddressUtil;
import com.luna.common.os.SystemInfoUtil;
import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luna
 * @date 2023/10/17
 */
@Configuration
public class DeviceConfig {

    public static final String LOCAL_IP = SystemInfoUtil.getNoLoopbackIP();

    public static final String LOOP_IP = "127.0.0.1";


    public static final String REMOTE_IP = "10.37.5.132";

    public static Map<String, Device> DEVICE_MAP = new ConcurrentHashMap<>();

    static {
        FromDevice clientFrom = FromDevice.getInstance("33010602011187000001", LOCAL_IP, 8118);
        DEVICE_MAP.put("client_from", clientFrom);

        ToDevice clientTo = ToDevice.getInstance("41010500002000000010", LOCAL_IP, 8117);
        clientTo.setPassword("luna");
        clientTo.setRealm("4101050000");
        DEVICE_MAP.put("client_to", clientTo);

        FromDevice serverFrom = FromDevice.getInstance("41010500002000000010", LOCAL_IP, 8117);
        serverFrom.setPassword("luna");
        serverFrom.setRealm("4101050000");
        DEVICE_MAP.put("server_from", serverFrom);

        ToDevice serverTo = ToDevice.getInstance("33010602011187000001", LOCAL_IP, 8118);
        DEVICE_MAP.put("server_to", serverTo);
    }

    @Bean
    @Qualifier("clientFrom")
    public Device clientFrom() {
        return DEVICE_MAP.get("client_from");
    }

    @Bean
    @Qualifier("clientTo")
    public Device clientTo() {
        return DEVICE_MAP.get("client_to");
    }

    @Bean
    @Qualifier("serverFrom")
    public Device serverDevice() {
        return DEVICE_MAP.get("server_from");
    }


    @Bean
    @Qualifier("serverTo")
    public Device cleientDevice() {
        return DEVICE_MAP.get("server_to");
    }
}
