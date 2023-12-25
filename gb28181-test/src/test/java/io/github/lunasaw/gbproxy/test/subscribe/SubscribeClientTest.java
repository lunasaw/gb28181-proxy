package io.github.lunasaw.gbproxy.test.subscribe;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.test.Gb28181ApplicationTest;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/12
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
public class SubscribeClientTest {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Autowired
    @Qualifier("clientTo")
    private Device toDevice;

    @AfterAll
    public static void after() {
        while (true) {

        }
    }

    @BeforeEach
    public void before() {
        // 本地端口监听
        log.info("before::客户端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        SipLayer.addListeningPoint(DeviceConfig.LOOP_IP, fromDevice.getPort(), true);

        DeviceConfig.DEVICE_CLIENT_VIEW_MAP.put(toDevice.getUserId(), toDevice);

    }

    @Test
    public void test_register() {
        ClientSendCmd.deviceRegister((FromDevice)fromDevice, (ToDevice)toDevice, 300, (eventResult) -> {
            test_register();
        });
    }
}
