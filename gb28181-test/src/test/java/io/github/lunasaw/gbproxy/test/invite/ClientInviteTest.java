package io.github.lunasaw.gbproxy.test.invite;

import javax.sip.message.Request;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.test.Gb28181ApplicationTest;
import io.github.lunasaw.gbproxy.test.user.client.DefaultRegisterProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import io.github.lunasaw.sip.common.service.DeviceSupplier;
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
public class ClientInviteTest {

    @Autowired
    private SipLayer       sipLayer;

    @Autowired
    private DeviceSupplier deviceSupplier;

    @AfterAll
    public static void after() {
        while (true) {

        }
    }

    @BeforeEach
    public void before() {
        // 获取客户端设备
        FromDevice fromDevice = (FromDevice)deviceSupplier.getDevice("33010602011187000001");
        ToDevice toDevice = (ToDevice)deviceSupplier.getDevice("41010500002000000001");

        if (fromDevice == null || toDevice == null) {
            log.error("未找到设备配置");
            return;
        }

        // 本地端口监听
        log.info("before::客户端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        sipLayer.addListeningPoint("127.0.0.1", fromDevice.getPort());
        // 模拟平台添加
        deviceSupplier.addOrUpdateDevice(toDevice);
    }

    @Test
    public void test_register_client() {
        String callId = SipRequestUtils.getNewCallId();

        // 获取设备
        FromDevice fromDevice = (FromDevice)deviceSupplier.getDevice("33010602011187000001");
        ToDevice toDevice = (ToDevice)deviceSupplier.getDevice("41010500002000000001");

        if (fromDevice == null || toDevice == null) {
            log.error("未找到设备配置");
            return;
        }

        Request registerRequest = SipRequestProvider.createRegisterRequest(fromDevice, toDevice, 300, callId);

        SipSender.transmitRequest(fromDevice.getIp(), registerRequest);
    }

    @Test
    public void b_test_un_register_client_custom() {
        // 获取设备
        FromDevice fromDevice = (FromDevice)deviceSupplier.getDevice("33010602011187000001");
        if (fromDevice == null) {
            log.error("未找到客户端设备配置");
            return;
        }

        Device instance = deviceSupplier.getDevice("41010500002000000001");
        DefaultRegisterProcessorClient.isRegister = false;
        ClientSendCmd.deviceUnRegister(fromDevice, (ToDevice)instance);
    }

}
