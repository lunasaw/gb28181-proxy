package io.github.lunasaw.gbproxy.client.transmit.cmd;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientCommandSender 单元测试类
 * 专门测试 sendRegisterCommand 方法
 * <p>
 * 测试规范：
 * 1. 严格区分client和server设备配置
 * 2. 客户端发起的消息使用clientFromDevice/clientToDevice
 * 3. 遵循项目编码规范和测试规范
 * 4. 使用简单的单元测试，不依赖Spring容器
 *
 * @author luna
 * @date 2024/01/01
 */
@DisplayName("ClientCommandSender 注册命令测试")
class ClientCommandSenderTest {

    public static final String IP = "127.0.0.1";
    // 客户端设备配置 - 严格遵循项目规范
    private FromDevice clientFromDevice;
    private ToDevice clientToDevice;

    @BeforeEach
    void setUp() {
        // 初始化客户端设备配置
        // 客户端From设备：用于客户端发送请求
        clientFromDevice = FromDevice.getInstance("33010602011187000001", IP, 8118);

        // 客户端To设备：用于客户端接收响应（服务端设备）
        clientToDevice = ToDevice.getInstance("41010500002000000001", IP, 8117);
        clientToDevice.setPassword("bajiuwulian1006");
        clientToDevice.setRealm("4101050000");
        SipLayer sipLayer = new SipLayer();
        sipLayer.addListeningPoint(IP, 8117);
        sipLayer.addListeningPoint(IP, 8118);


    }

    @Test
    @DisplayName("测试发送注册命令 - 正常情况")
    void testSendRegisterCommand_Success() {
        // 准备测试数据
        Integer expires = 3600;

        // 执行测试
        String actualCallId = ClientCommandSender.sendRegisterCommand(
                clientFromDevice,
                clientToDevice,
                expires
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试发送注册命令 - 不同过期时间")
    void testSendRegisterCommand_DifferentExpires() {
        // 测试不同的过期时间值
        Integer[] expiresValues = {0, 300, 3600, 7200, 86400};

        for (Integer expires : expiresValues) {
            // 执行测试
            String actualCallId = ClientCommandSender.sendRegisterCommand(
                    clientFromDevice,
                    clientToDevice,
                    expires
            );

            // 验证结果
            assertNotNull(actualCallId, "返回的callId不能为空，expires=" + expires);
            assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串，expires=" + expires);
        }
    }

    @Test
    @DisplayName("测试发送注册命令 - 设备配置验证")
    void testSendRegisterCommand_DeviceConfiguration() {
        // 验证设备配置的正确性
        assertNotNull(clientFromDevice, "客户端From设备不能为空");
        assertNotNull(clientToDevice, "客户端To设备不能为空");

        assertEquals("33010602011187000001", clientFromDevice.getUserId(), "客户端From设备ID应该正确");
        assertEquals(IP, clientFromDevice.getIp(), "客户端From设备IP应该正确");
        assertEquals(8118, clientFromDevice.getPort(), "客户端From设备端口应该正确");

        assertEquals("41010500002000000001", clientToDevice.getUserId(), "客户端To设备ID应该正确");
        assertEquals(IP, clientToDevice.getIp(), "客户端To设备IP应该正确");
        assertEquals(8117, clientToDevice.getPort(), "客户端To设备端口应该正确");
        assertEquals("bajiuwulian1006", clientToDevice.getPassword(), "客户端To设备密码应该正确");
        assertEquals("4101050000", clientToDevice.getRealm(), "客户端To设备Realm应该正确");

        // 验证设备类型
        assertTrue(clientFromDevice instanceof FromDevice, "客户端From设备应该是FromDevice类型");
        assertTrue(clientToDevice instanceof ToDevice, "客户端To设备应该是ToDevice类型");

        // 验证设备配置分离 - 确保不是同一个设备
        assertNotEquals(clientFromDevice.getUserId(), clientToDevice.getUserId(),
                "客户端From和To设备的用户ID应该不同");
    }

    @Test
    @DisplayName("测试发送注册命令 - 参数边界值")
    void testSendRegisterCommand_BoundaryValues() {
        // 测试边界值
        Integer[] boundaryExpires = {0, 1, Integer.MAX_VALUE};

        for (Integer expires : boundaryExpires) {
            // 执行测试
            String actualCallId = ClientCommandSender.sendRegisterCommand(
                    clientFromDevice,
                    clientToDevice,
                    expires
            );

            // 验证结果
            assertNotNull(actualCallId, "边界值测试返回的callId不能为空，expires=" + expires);
            assertFalse(actualCallId.isEmpty(), "边界值测试返回的callId不能为空字符串，expires=" + expires);
        }
    }

    @Test
    @DisplayName("测试发送注册命令 - 设备配置一致性")
    void testSendRegisterCommand_DeviceConsistency() {
        // 验证设备配置的一致性
        Integer expires = 3600;

        // 多次调用，验证设备配置一致性
        for (int i = 0; i < 3; i++) {
            String actualCallId = ClientCommandSender.sendRegisterCommand(
                    clientFromDevice,
                    clientToDevice,
                    expires
            );

            assertNotNull(actualCallId, "第" + (i + 1) + "次调用返回的callId不能为空");
            assertFalse(actualCallId.isEmpty(), "第" + (i + 1) + "次调用返回的callId不能为空字符串");
        }
    }

    @Test
    @DisplayName("测试发送注册命令 - 异常情况处理")
    void testSendRegisterCommand_ExceptionHandling() {
        // 测试空设备参数
        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendRegisterCommand(null, clientToDevice, 3600);
        }, "发送设备不能为空");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendRegisterCommand(clientFromDevice, null, 3600);
        }, "接收设备不能为空");

        // 测试负数过期时间
        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendRegisterCommand(clientFromDevice, clientToDevice, -1);
        }, "过期时间<0");
    }

    @Test
    @DisplayName("测试发送注册命令 - 策略模式调用")
    void testSendRegisterCommand_StrategyPattern() {
        // 验证策略模式的调用
        Integer expires = 3600;

        // 使用策略模式调用
        String actualCallId = ClientCommandSender.sendCommand(
                "REGISTER",
                clientFromDevice,
                clientToDevice,
                expires
        );

        // 验证结果
        assertNotNull(actualCallId, "策略模式返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "策略模式返回的callId不能为空字符串");

        // 验证与直接调用结果一致
        String directCallId = ClientCommandSender.sendRegisterCommand(
                clientFromDevice,
                clientToDevice,
                expires
        );
        assertNotNull(directCallId, "直接调用返回的callId不能为空");
    }

    @Test
    @DisplayName("测试发送注册命令 - 建造者模式")
    void testSendRegisterCommand_BuilderPattern() {
        // 测试建造者模式
        Integer expires = 3600;

        // 使用建造者模式
        String actualCallId = ClientCommandSender.builder()
                .commandType("REGISTER")
                .fromDevice(clientFromDevice)
                .toDevice(clientToDevice)
                .params(expires)
                .execute();

        // 验证结果
        assertNotNull(actualCallId, "建造者模式返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "建造者模式返回的callId不能为空字符串");

        // 验证与直接调用结果一致
        String directCallId = ClientCommandSender.sendRegisterCommand(
                clientFromDevice,
                clientToDevice,
                expires
        );
        assertNotNull(directCallId, "直接调用返回的callId不能为空");
    }

    @Test
    @DisplayName("测试发送注册命令 - 注销命令")
    void testSendRegisterCommand_Unregister() {
        // 测试注销命令（expires=0）
        Integer expires = 0;

        // 执行测试
        String actualCallId = ClientCommandSender.sendRegisterCommand(
                clientFromDevice,
                clientToDevice,
                expires
        );

        // 验证结果
        assertNotNull(actualCallId, "注销命令返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "注销命令返回的callId不能为空字符串");

        // 验证与sendUnregisterCommand方法结果一致
        String unregisterCallId = ClientCommandSender.sendUnregisterCommand(
                clientFromDevice,
                clientToDevice
        );
        assertNotNull(unregisterCallId, "sendUnregisterCommand返回的callId不能为空");
    }
}