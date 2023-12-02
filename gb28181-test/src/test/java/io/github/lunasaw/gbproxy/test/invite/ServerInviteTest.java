package io.github.lunasaw.gbproxy.test.invite;

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
public class ServerInviteTest {

    @Autowired
    @Qualifier("serverFrom")
    private Device      fromDevice;

    @Autowired
    private DynamicTask dynamicTask;

    @BeforeEach
    public void before() {
        // 本地端口监听
        SipLayer.addListeningPoint(DeviceConfig.LOOP_IP, 8117, true);
    }

    @Test
    @SneakyThrows
    public void test_invite_server() {
        dynamicTask.startDelay("play_test", () -> {
            Device device = DeviceConfig.DEVICE_SERVER_VIEW_MAP.get("33010602011187000001");
            if (device == null) {
                test_invite_server();
                return;
            }
            String invitePlay = ServerSendCmd.deviceInvitePlay((FromDevice)fromDevice, (ToDevice)device, "127.0.0.1", 1554);
        }, 10 * 1000);
    }

    @Test
    @SneakyThrows
    public void test_invite_play_back_server() {
        dynamicTask.startDelay("play_back_test", () -> {
            Device device = DeviceConfig.DEVICE_SERVER_VIEW_MAP.get("34020000001320000001");
            if (device == null) {
                test_invite_play_back_server();
                return;
            }
            String invitePlay =
                ServerSendCmd.deviceInvitePlayBack((FromDevice)fromDevice, (ToDevice)device, "127.0.0.1", 10000, "2023-11-29 00:00:00");
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
