package io.github.lunasaw.gbproxy.client.user;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.luna.common.os.SystemInfoUtil;

import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

/**
 * @author luna
 * @date 2023/10/17
 */
@Configuration
public class DeviceClientConfig {

    public static final String        LOOP_IP                = SystemInfoUtil.getIpv4();

    public static Map<String, Device> DEVICE_MAP             = new ConcurrentHashMap<>();

    public static Map<String, Device> DEVICE_CLIENT_VIEW_MAP = new ConcurrentHashMap<>();

    static {
        FromDevice clientFrom = FromDevice.getInstance("33010602011187000001", LOOP_IP, 8118);
        DEVICE_MAP.put("client_from", clientFrom);

        ToDevice clientTo = ToDevice.getInstance("41010500002000000001", LOOP_IP, 8117);
        clientTo.setPassword("bajiuwulian1006");
        clientTo.setRealm("4101050000");
        DEVICE_MAP.put("client_to", clientTo);
    }

    @Bean
    @ConditionalOnMissingBean(name = {"clientFrom"})
    public Device clientFrom() {
        return DEVICE_MAP.get("client_from");
    }

    @Bean
    @ConditionalOnMissingBean(name = {"clientTo"})
    public Device clientTo() {
        return DEVICE_MAP.get("client_to");
    }
}
