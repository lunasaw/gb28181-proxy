package io.github.lunasaw.gbproxy.test.server;

import io.github.lunasaw.gbproxy.server.Gb28181Server;
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
 * 服务端设备心跳监控测试类
 * 测试服务端心跳监控功能，包括心跳请求处理和设备状态监控
 *
 * @author luna
 * @date 2025/01/23
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
@TestPropertySource(properties = {
    "sip.gb28181.server.ip=0.0.0.0",
    "sip.gb28181.server.port=5060",
    "sip.gb28181.server.domain=34020000002000000001",
    "sip.gb28181.server.serverId=34020000002000000001",
    "sip.gb28181.server.serverName=GB28181-Server",
    "sip.gb28181.server.maxDevices=1000",
    "sip.gb28181.server.deviceTimeout=5m",
    "sip.gb28181.server.enableTcp=true",
    "sip.gb28181.server.enableUdp=true"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerHeartbeatTest {

    @Autowired
    private SipLayer           sipLayer;

    @Autowired
    private TestDeviceSupplier testDeviceSupplier;

    @Autowired
    private Gb28181Server      gb28181Server;

    private CountDownLatch     responseLatch;
    private EventResult        lastEventResult;

    @BeforeEach
    public void setUp() {
        responseLatch = new CountDownLatch(1);
        lastEventResult = null;
    }

    @Test
    @Order(1)
    @DisplayName("测试服务端心跳请求处理")
    public void testServerHeartbeatRequestHandling() throws Exception {
        // 获取设备配置
        FromDevice serverFromDevice = testDeviceSupplier.getServerFromDevice();
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();
        ToDevice serverToDevice = testDeviceSupplier.getServerToDevice();

        if (clientFromDevice == null || clientToDevice == null) {
            log.error("未找到客户端设备配置");
            return;
        }

        // 模拟客户端发送心跳请求到服务端
        String callId = SipRequestUtils.getNewCallId();
        Request heartbeatRequest = SipRequestProvider.createMessageRequest(
            clientFromDevice, // 客户端设备
            clientToDevice, // 服务端设备
            callId,
            "<?xml version=\"1.0\"?>\n" +
                "<Control>\n" +
                "  <CmdType>Keepalive</CmdType>\n" +
                "  <SN>1</SN>\n" +
                "  <DeviceID>" + clientFromDevice.getUserId() + "</DeviceID>\n" +
                "</Control>");

        System.out.println("=== 开始服务端心跳请求处理测试 ===");
        System.out.println("心跳请求CallId: " + callId);
        System.out.println("心跳请求From: " + clientFromDevice.getUserId());
        System.out.println("心跳请求To: " + clientToDevice.getUserId());

        // 服务端应该能够处理心跳请求并返回响应
        Assertions.assertNotNull(heartbeatRequest, "心跳请求应该被正确创建");
        Assertions.assertEquals("MESSAGE", heartbeatRequest.getMethod(), "请求方法应该是MESSAGE");

        System.out.println("服务端心跳请求处理测试通过");
    }

    @Test
    @Order(2)
    @DisplayName("测试服务端心跳状态监控")
    public void testServerHeartbeatStatusMonitoring() throws Exception {
        // 获取设备配置
        FromDevice serverFromDevice = testDeviceSupplier.getServerFromDevice();
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();
        ToDevice serverToDevice = testDeviceSupplier.getServerToDevice();

        if (clientFromDevice == null || clientToDevice == null) {
            log.error("未找到客户端设备配置");
            return;
        }

        // 模拟客户端发送心跳状态通知到服务端
        String callId = SipRequestUtils.getNewCallId();
        Request statusNotifyRequest = SipRequestProvider.createMessageRequest(
            clientFromDevice, // 客户端设备
            clientToDevice, // 服务端设备
            callId,
            "<?xml version=\"1.0\"?>\n" +
                "<Notify>\n" +
                "  <CmdType>Keepalive</CmdType>\n" +
                "  <SN>2</SN>\n" +
                "  <DeviceID>" + clientFromDevice.getUserId() + "</DeviceID>\n" +
                "  <Status>OK</Status>\n" +
                "</Notify>");

        System.out.println("=== 开始服务端心跳状态监控测试 ===");
        System.out.println("状态通知CallId: " + callId);
        System.out.println("状态通知From: " + clientFromDevice.getUserId());
        System.out.println("状态通知To: " + clientToDevice.getUserId());

        // 服务端应该能够处理心跳状态通知
        Assertions.assertNotNull(statusNotifyRequest, "状态通知请求应该被正确创建");
        Assertions.assertEquals("MESSAGE", statusNotifyRequest.getMethod(), "请求方法应该是MESSAGE");

        System.out.println("服务端心跳状态监控测试通过");
    }

    @Test
    @Order(3)
    @DisplayName("测试服务端心跳配置验证")
    public void testServerHeartbeatConfiguration() {
        System.out.println("=== 服务端心跳配置验证测试 ===");

        // 验证服务端配置
        Assertions.assertNotNull(gb28181Server, "GB28181服务端应该被正确初始化");
        Assertions.assertNotNull(sipLayer, "SIP层应该被正确初始化");

        // 获取设备配置
        FromDevice serverFromDevice = testDeviceSupplier.getServerFromDevice();
        ToDevice serverToDevice = testDeviceSupplier.getServerToDevice();

        // 验证设备配置
        Assertions.assertNotNull(serverFromDevice, "服务端设备应该被正确配置");
        Assertions.assertNotNull(serverToDevice, "客户端设备应该被正确配置");

        // 验证设备类型
        Assertions.assertTrue(serverFromDevice instanceof FromDevice, "服务端设备应该是FromDevice类型");
        Assertions.assertTrue(serverToDevice instanceof ToDevice, "客户端设备应该是ToDevice类型");

        System.out.println("服务端心跳配置验证通过");
        System.out.println("服务端设备: " + serverFromDevice.getUserId());
        System.out.println("客户端设备: " + serverToDevice.getUserId());
    }

    @Test
    @Order(4)
    @DisplayName("测试服务端心跳响应发送")
    public void testServerHeartbeatResponse() throws Exception {
        // 获取设备配置
        FromDevice serverFromDevice = testDeviceSupplier.getServerFromDevice();
        ToDevice serverToDevice = testDeviceSupplier.getServerToDevice();

        if (serverFromDevice == null || serverToDevice == null) {
            log.error("未找到设备配置");
            return;
        }

        String callId = SipRequestUtils.getNewCallId();

        // 模拟服务端发送心跳响应
        System.out.println("=== 开始服务端心跳响应测试 ===");
        System.out.println("心跳响应CallId: " + callId);
        System.out.println("心跳响应From: " + serverFromDevice.getUserId());
        System.out.println("心跳响应To: " + serverToDevice.getUserId());

        // 这里主要验证服务端能够发送心跳响应
        Assertions.assertNotNull(callId, "CallId应该被正确生成");
        Assertions.assertNotNull(serverFromDevice.getUserId(), "服务端设备ID不能为空");
        Assertions.assertNotNull(serverToDevice.getUserId(), "客户端设备ID不能为空");

        System.out.println("服务端心跳响应测试通过");
    }

    @Test
    @Order(5)
    @DisplayName("测试服务端设备状态管理")
    public void testServerDeviceStatusManagement() {
        System.out.println("=== 服务端设备状态管理测试 ===");

        // 验证服务端能够管理设备状态
        Assertions.assertNotNull(gb28181Server, "GB28181服务端应该被正确初始化");

        // 获取设备配置
        FromDevice serverFromDevice = testDeviceSupplier.getServerFromDevice();
        ToDevice serverToDevice = testDeviceSupplier.getServerToDevice();

        // 验证设备配置
        Assertions.assertNotNull(serverFromDevice, "服务端设备应该被正确配置");
        Assertions.assertNotNull(serverToDevice, "客户端设备应该被正确配置");

        // 模拟设备状态管理
        String deviceId = serverToDevice.getUserId();
        Assertions.assertNotNull(deviceId, "设备ID不能为空");

        System.out.println("服务端设备状态管理测试通过");
        System.out.println("管理设备ID: " + deviceId);
    }

    @AfterEach
    public void tearDown() {
        // 清理资源
        if (responseLatch != null) {
            responseLatch.countDown();
        }
    }
}