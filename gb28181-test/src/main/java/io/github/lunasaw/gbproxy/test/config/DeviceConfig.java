package io.github.lunasaw.gbproxy.test.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.luna.common.os.SystemInfoUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.lunasaw.gbproxy.test.user.client.DefaultRegisterProcessorClient;
import io.github.lunasaw.gbproxy.test.user.server.DefaultRegisterProcessorServer;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import oshi.SystemInfo;

/**
 * @author luna
 * @date 2023/10/17
 */
@Configuration
public class DeviceConfig {

    public static final String LOOP_IP = SystemInfoUtil.getIpv4();

    public static final String LOOP_IP_LOCAL = "0.0.0.0";

    public static final String REMOTE_IP = "10.37.5.132";

    public static Map<String, Device> DEVICE_MAP = new ConcurrentHashMap<>();

    public static Map<String, Device> DEVICE_CLIENT_VIEW_MAP = new ConcurrentHashMap<>();

    public static Map<String, Device> DEVICE_SERVER_VIEW_MAP = new ConcurrentHashMap<>();

    static {
        FromDevice clientFrom = FromDevice.getInstance("33010602011187000001", LOOP_IP, 8118);
        DEVICE_MAP.put("client_from", clientFrom);

        ToDevice clientTo = ToDevice.getInstance("41010500002000000001", LOOP_IP, 8117);
        clientTo.setPassword("bajiuwulian1006");
        clientTo.setRealm("4101050000");
        DEVICE_MAP.put("client_to", clientTo);

        FromDevice serverFrom = FromDevice.getInstance("41010500002000000001", LOOP_IP, 8117);
        serverFrom.setPassword("bajiuwulian1006");
        serverFrom.setRealm("4101050000");
        DEVICE_MAP.put("server_from", serverFrom);

        ToDevice serverTo = ToDevice.getInstance("33010602011187000001", LOOP_IP, 8118);
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
