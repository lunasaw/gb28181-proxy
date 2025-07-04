package io.github.lunasaw.gbproxy.test.invite;

import org.junit.jupiter.api.*;
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
@SpringBootTest(classes = Gb28181ApplicationTest.class,
    webEnvironment = SpringBootTest.WebEnvironment.NONE,
    properties = "spring.profiles.active=test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerInviteTest {

    @Autowired
    private DynamicTask dynamicTask;

    @Autowired
    private SipLayer    sipLayer;

    @Autowired
    private DeviceSupplier deviceSupplier;

    @BeforeEach
    public void before() {
        // 本地端口监听
        sipLayer.addListeningPoint("127.0.0.1", 8117, true);
    }

    @Test
    @SneakyThrows
    public void test_invite_server() {
        dynamicTask.startDelay("play_test", () -> {
            Device device = deviceSupplier.getDevice("33010602011187000001");
            if (device == null) {
                test_invite_server();
                return;
            }

            // 获取服务端设备
            FromDevice fromDevice = (FromDevice)deviceSupplier.getDevice("33010602011187000001");

            if (fromDevice == null) {
                log.error("未找到服务端设备配置");
                return;
            }

            String invitePlay = ServerSendCmd.deviceInvitePlay(fromDevice, (ToDevice)device, "127.0.0.1", 1554);
        }, 10 * 1000);
    }

    @Test
    @SneakyThrows
    public void test_invite_play_back_server() {
        dynamicTask.startDelay("play_back_test", () -> {
            Device device = deviceSupplier.getDevice("34020000001320000001");
            if (device == null) {
                test_invite_play_back_server();
                return;
            }

            // 获取服务端设备
            FromDevice fromDevice = (FromDevice)deviceSupplier.getDevice("33010602011187000001");
            if (fromDevice == null) {
                log.error("未找到服务端设备配置");
                return;
            }

            String invitePlay =
                ServerSendCmd.deviceInvitePlayBack(fromDevice, (ToDevice)device, "127.0.0.1", 10000, "2023-11-29 00:00:00");
            System.out.println(invitePlay);
        }, 30 * 1000);
    }

    @SneakyThrows
    @AfterEach
    public void after() {
        while (true) {

        }
    }
}
