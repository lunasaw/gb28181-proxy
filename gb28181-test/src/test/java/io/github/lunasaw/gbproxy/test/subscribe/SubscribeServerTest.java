package io.github.lunasaw.gbproxy.test.subscribe;

import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.lunasaw.gbproxy.server.transimit.cmd.ServerSendCmd;
import io.github.lunasaw.gbproxy.test.Gb28181ApplicationTest;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.utils.DynamicTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/12
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
public class SubscribeServerTest {

    @Autowired
    @Qualifier("serverFrom")
    private Device      fromDevice;

    @Autowired
    @Qualifier("serverTo")
    private Device      toDevice;

    @Autowired
    private DynamicTask dynamicTask;

    @Autowired
    private SipLayer sipLayer;

    @BeforeEach
    public void before() {
        // 本地端口监听
        log.info("before::服务端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, 8117, true);
    }

    @Test
    public void test_register_server() throws Exception {

    }

    @Test
    public void test_subscribe() {
        dynamicTask.startDelay("test_subscribe", () -> {
            Device device = DeviceConfig.DEVICE_SERVER_VIEW_MAP.get("33010602011187000001");
            if (device == null) {
                test_subscribe();
                return;
            }
            String invitePlay =
                ServerSendCmd.deviceCatalogSubscribe((FromDevice)fromDevice, (ToDevice)device, 30, CmdTypeEnum.CATALOG.getType());
            log.info("test_subscribe:: callId = {}", invitePlay);
        }, 30 * 1000);
    }

    @SneakyThrows
    @AfterEach
    public void after() {
        while (true) {

        }
    }
}
