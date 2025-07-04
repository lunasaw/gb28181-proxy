package io.github.lunasaw.gbproxy.test.subscribe;

import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.gbproxy.test.config.TestDeviceSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.lunasaw.gbproxy.server.transimit.cmd.ServerSendCmd;
import io.github.lunasaw.gbproxy.test.Gb28181ApplicationTest;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.utils.DynamicTask;
import io.github.lunasaw.sip.common.service.DeviceSupplier;
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
    private DynamicTask dynamicTask;

    @Autowired
    private SipLayer sipLayer;

    @Autowired
    private DeviceSupplier deviceSupplier;

    @Autowired
    private TestDeviceSupplier testDeviceSupplier;

    @BeforeEach
    public void before() {
        // 获取服务端设备
        FromDevice fromDevice = testDeviceSupplier.getServerFromDevice();
        if (fromDevice == null) {
            log.error("未找到服务端设备配置");
            return;
        }

        // 本地端口监听
        log.info("before::服务端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        sipLayer.addListeningPoint("127.0.0.1", 8117, true);
    }

    @Test
    public void test_register_server() throws Exception {

    }

    @Test
    public void test_subscribe() {
        dynamicTask.startDelay("test_subscribe", () -> {
            Device device = testDeviceSupplier.getDevice("33010602011187000001");
            if (device == null) {
                test_subscribe();
                return;
            }

            // 获取服务端设备
            FromDevice fromDevice = testDeviceSupplier.getServerFromDevice();

            if (fromDevice == null) {
                log.error("未找到服务端设备配置");
                return;
            }

            String invitePlay =
                ServerSendCmd.deviceCatalogSubscribe(fromDevice, (ToDevice)device, 30, CmdTypeEnum.CATALOG.getType());
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
