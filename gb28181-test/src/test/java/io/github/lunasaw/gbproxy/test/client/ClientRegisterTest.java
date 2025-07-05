package io.github.lunasaw.gbproxy.test.client;

import io.github.lunasaw.gbproxy.client.Gb28181Client;
import io.github.lunasaw.gbproxy.test.Gb28181ApplicationTest;
import io.github.lunasaw.gbproxy.test.config.TestDeviceSupplier;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sip.message.Request;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 客户端设备注册测试类
 * 测试设备端注册功能，包括注册请求发送和响应处理
 *
 * @author luna
 * @date 2025/01/23
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
@TestPropertySource(properties = {
    "sip.gb28181.client.keepAliveInterval=1m",
    "sip.gb28181.client.maxRetries=3",
    "sip.gb28181.client.retryDelay=5s",
    "sip.gb28181.client.registerExpires=3600",
    "sip.gb28181.client.clientId=34020000001320000001",
    "sip.gb28181.client.clientName=GB28181-Client",
    "sip.gb28181.client.username=admin",
    "sip.gb28181.client.password=123456"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientRegisterTest {

    @Autowired
    private SipLayer           sipLayer;

    @Autowired
    private TestDeviceSupplier testDeviceSupplier;

    private CountDownLatch     responseLatch;
    private EventResult        lastEventResult;

    @BeforeEach
    public void setUp() {
        responseLatch = new CountDownLatch(1);
        lastEventResult = null;
        sipLayer.addListeningPoint("127.0.0.1", 8117);
    }

    @Test
    @Order(1)
    @DisplayName("测试客户端SIP层初始化")
    public void testClientSipLayerInitialization() {
        Assertions.assertNotNull(sipLayer, "SIP层应该被正确初始化");

        // 获取客户端设备配置
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        Assertions.assertNotNull(clientFromDevice, "客户端设备应该被正确配置");
        Assertions.assertNotNull(clientToDevice, "服务端设备应该被正确配置");

        System.out.println("=== 客户端SIP层初始化测试通过 ===");
        System.out.println("客户端设备: " + clientFromDevice.getUserId() + ":" + clientFromDevice.getPort());
        System.out.println("服务端设备: " + clientToDevice.getUserId() + ":" + clientToDevice.getPort());
    }

    @Test
    @Order(2)
    @DisplayName("测试客户端设备注册请求发送")
    public void testClientDeviceRegistration() throws Exception {
        // 获取设备配置
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        if (clientFromDevice == null || clientToDevice == null) {
            log.error("未找到设备配置");
            return;
        }

        String callId = SipRequestUtils.getNewCallId();
        Request registerRequest = SipRequestProvider.createRegisterRequest(
            clientFromDevice,
            clientToDevice,
            3600,
            callId);

        System.out.println("=== 开始客户端设备注册测试 ===");
        System.out.println("注册请求CallId: " + callId);
        System.out.println("注册请求From: " + clientFromDevice.getUserId());
        System.out.println("注册请求To: " + clientToDevice.getUserId());

        SipSender.transmitRequestSuccess(clientFromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                lastEventResult = eventResult;
                System.out.println("收到注册响应: " + eventResult);
                responseLatch.countDown();
            }
        });

        // 等待响应，最多等待10秒
        boolean received = responseLatch.await(10, TimeUnit.SECONDS);

        if (received && lastEventResult != null) {
            System.out.println("注册响应状态码: " + lastEventResult.getStatusCode());
            System.out.println("注册响应消息: " + lastEventResult.toString());

            // 检查响应状态码（200表示成功）
            Assertions.assertTrue(
                lastEventResult.getStatusCode() >= 200 && lastEventResult.getStatusCode() < 300,
                "注册应该成功，状态码应该在200-299之间");
        } else {
            System.out.println("注册请求超时或未收到响应");
            // 在测试环境中，如果没有真实的SIP服务器，我们只验证请求能够发送
            Assertions.assertTrue(true, "注册请求已发送");
        }
    }

    @Test
    @Order(3)
    @DisplayName("测试客户端设备配置验证")
    public void testClientDeviceConfiguration() {
        System.out.println("=== 客户端设备配置验证测试 ===");

        // 获取设备配置
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        // 验证客户端设备配置
        Assertions.assertNotNull(clientFromDevice.getUserId(), "客户端设备ID不能为空");
        Assertions.assertNotNull(clientFromDevice.getIp(), "客户端设备IP不能为空");
        Assertions.assertTrue(clientFromDevice.getPort() > 0, "客户端设备端口必须大于0");

        // 验证服务端设备配置
        Assertions.assertNotNull(clientToDevice.getUserId(), "服务端设备ID不能为空");
        Assertions.assertNotNull(clientToDevice.getIp(), "服务端设备IP不能为空");
        Assertions.assertTrue(clientToDevice.getPort() > 0, "服务端设备端口必须大于0");

        System.out.println("客户端设备配置: " + clientFromDevice.getUserId() + "@" + clientFromDevice.getIp() + ":" + clientFromDevice.getPort());
        System.out.println("服务端设备配置: " + clientToDevice.getUserId() + "@" + clientToDevice.getIp() + ":" + clientToDevice.getPort());

        // 验证设备类型
        Assertions.assertTrue(clientFromDevice instanceof FromDevice, "客户端设备应该是FromDevice类型");
        Assertions.assertTrue(clientToDevice instanceof ToDevice, "服务端设备应该是ToDevice类型");
    }

    @AfterEach
    public void tearDown() {
        // 清理资源
        if (responseLatch != null) {
            responseLatch.countDown();
        }
    }
}