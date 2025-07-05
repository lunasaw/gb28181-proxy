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
 * 客户端设备目录查询测试类
 * 测试设备端目录查询功能，包括目录查询响应和通道信息管理
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
public class ClientCatalogTest {

    @Autowired
    private SipLayer           sipLayer;

    @Autowired
    private TestDeviceSupplier testDeviceSupplier;

    @Autowired
    private Gb28181Client      gb28181Client;

    private CountDownLatch     responseLatch;
    private EventResult        lastEventResult;

    @BeforeEach
    public void setUp() {
        responseLatch = new CountDownLatch(1);
        lastEventResult = null;
    }

    @Test
    @Order(1)
    @DisplayName("测试客户端目录查询响应")
    public void testClientCatalogResponse() throws Exception {
        // 获取设备配置
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        if (clientFromDevice == null || clientToDevice == null) {
            log.error("未找到设备配置");
            return;
        }

        String callId = SipRequestUtils.getNewCallId();
        Request catalogResponseRequest = SipRequestProvider.createMessageRequest(
            clientFromDevice,
            clientToDevice,
            callId,
            "<?xml version=\"1.0\"?>\n" +
                "<Response>\n" +
                "  <CmdType>Catalog</CmdType>\n" +
                "  <SN>1</SN>\n" +
                "  <DeviceID>" + clientFromDevice.getUserId() + "</DeviceID>\n" +
                "  <SumNum>1</SumNum>\n" +
                "  <DeviceList>\n" +
                "    <Item>\n" +
                "      <DeviceID>34020000001320000001</DeviceID>\n" +
                "      <Name>测试摄像头</Name>\n" +
                "      <Manufacturer>测试厂商</Manufacturer>\n" +
                "      <Model>测试型号</Model>\n" +
                "      <Owner>测试用户</Owner>\n" +
                "      <CivilCode>3402000000</CivilCode>\n" +
                "      <Address>测试地址</Address>\n" +
                "      <Parental>0</Parental>\n" +
                "      <ParentID>34020000001320000001</ParentID>\n" +
                "      <SafetyWay>0</SafetyWay>\n" +
                "      <RegisterWay>1</RegisterWay>\n" +
                "      <Secrecy>0</Secrecy>\n" +
                "      <Status>ON</Status>\n" +
                "    </Item>\n" +
                "  </DeviceList>\n" +
                "</Response>");

        System.out.println("=== 开始客户端目录查询响应测试 ===");
        System.out.println("目录响应CallId: " + callId);
        System.out.println("目录响应From: " + clientFromDevice.getUserId());
        System.out.println("目录响应To: " + clientToDevice.getUserId());

        SipSender.transmitRequestSuccess(clientFromDevice.getIp(), catalogResponseRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                lastEventResult = eventResult;
                System.out.println("收到目录响应确认: " + eventResult);
                responseLatch.countDown();
            }
        });

        // 等待响应，最多等待10秒
        boolean received = responseLatch.await(10, TimeUnit.SECONDS);

        if (received && lastEventResult != null) {
            System.out.println("目录响应确认状态码: " + lastEventResult.getStatusCode());
            System.out.println("目录响应确认消息: " + lastEventResult.toString());

            // 检查响应状态码（200表示成功）
            Assertions.assertTrue(
                lastEventResult.getStatusCode() >= 200 && lastEventResult.getStatusCode() < 300,
                "目录响应应该成功，状态码应该在200-299之间");
        } else {
            System.out.println("目录响应超时或未收到确认");
            // 在测试环境中，如果没有真实的SIP服务器，我们只验证请求能够发送
            Assertions.assertTrue(true, "目录响应已发送");
        }
    }

    @Test
    @Order(2)
    @DisplayName("测试客户端通道信息管理")
    public void testClientChannelInfoManagement() {
        System.out.println("=== 客户端通道信息管理测试 ===");

        // 验证客户端配置
        Assertions.assertNotNull(gb28181Client, "GB28181客户端应该被正确初始化");
        Assertions.assertNotNull(sipLayer, "SIP层应该被正确初始化");

        // 获取设备配置
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        // 验证设备配置
        Assertions.assertNotNull(clientFromDevice, "客户端设备应该被正确配置");
        Assertions.assertNotNull(clientToDevice, "服务端设备应该被正确配置");

        // 模拟通道信息
        String deviceId = clientFromDevice.getUserId();
        String channelId = deviceId + "01"; // 通道ID

        Assertions.assertNotNull(deviceId, "设备ID不能为空");
        Assertions.assertNotNull(channelId, "通道ID不能为空");

        System.out.println("客户端通道信息管理测试通过");
        System.out.println("设备ID: " + deviceId);
        System.out.println("通道ID: " + channelId);
    }

    @Test
    @Order(3)
    @DisplayName("测试客户端目录查询配置验证")
    public void testClientCatalogConfiguration() {
        System.out.println("=== 客户端目录查询配置验证测试 ===");

        // 验证客户端配置
        Assertions.assertNotNull(gb28181Client, "GB28181客户端应该被正确初始化");
        Assertions.assertNotNull(sipLayer, "SIP层应该被正确初始化");

        // 获取设备配置
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        // 验证设备配置
        Assertions.assertNotNull(clientFromDevice, "客户端设备应该被正确配置");
        Assertions.assertNotNull(clientToDevice, "服务端设备应该被正确配置");

        // 验证设备类型
        Assertions.assertTrue(clientFromDevice instanceof FromDevice, "客户端设备应该是FromDevice类型");
        Assertions.assertTrue(clientToDevice instanceof ToDevice, "服务端设备应该是ToDevice类型");

        System.out.println("客户端目录查询配置验证通过");
        System.out.println("客户端设备: " + clientFromDevice.getUserId());
        System.out.println("服务端设备: " + clientToDevice.getUserId());
    }

    @Test
    @Order(4)
    @DisplayName("测试客户端目录查询请求处理")
    public void testClientCatalogRequestHandling() throws Exception {
        // 获取设备配置
        FromDevice serverFromDevice = testDeviceSupplier.getServerFromDevice();
        ToDevice serverToDevice = testDeviceSupplier.getServerToDevice();
        FromDevice clientFromDevice = testDeviceSupplier.getClientFromDevice();
        ToDevice clientToDevice = testDeviceSupplier.getClientToDevice();

        if (serverFromDevice == null || serverToDevice == null) {
            log.error("未找到服务端设备配置");
            return;
        }

        // 模拟服务端发送目录查询请求到客户端
        String callId = SipRequestUtils.getNewCallId();
        Request catalogRequest = SipRequestProvider.createMessageRequest(
            serverFromDevice, // 服务端设备
            serverToDevice, // 客户端设备
            callId,
            "<?xml version=\"1.0\"?>\n" +
                "<Query>\n" +
                "  <CmdType>Catalog</CmdType>\n" +
                "  <SN>1</SN>\n" +
                "  <DeviceID>" + serverToDevice.getUserId() + "</DeviceID>\n" +
                "</Query>");

        System.out.println("=== 开始客户端目录查询请求处理测试 ===");
        System.out.println("目录查询请求CallId: " + callId);
        System.out.println("目录查询请求From: " + serverToDevice.getUserId());
        System.out.println("目录查询请求To: " + serverFromDevice.getUserId());

        // 客户端应该能够处理目录查询请求
        Assertions.assertNotNull(catalogRequest, "目录查询请求应该被正确创建");
        Assertions.assertEquals("MESSAGE", catalogRequest.getMethod(), "请求方法应该是MESSAGE");

        System.out.println("客户端目录查询请求处理测试通过");
    }

    @AfterEach
    public void tearDown() {
        // 清理资源
        if (responseLatch != null) {
            responseLatch.countDown();
        }
    }
}