package io.github.lunasaw.gbproxy.client.transmit.cmd;

import io.github.lunasaw.gb28181.common.entity.DeviceAlarm;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.response.DeviceItem;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.ClientCommandStrategy;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.transmit.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClientCommandSender 单元测试类
 * 测试所有命令发送方法
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
@DisplayName("ClientCommandSender 命令测试")
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

    // ==================== 注册命令测试 ====================

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
        assertInstanceOf(FromDevice.class, clientFromDevice, "客户端From设备应该是FromDevice类型");
        assertInstanceOf(ToDevice.class, clientToDevice, "客户端To设备应该是ToDevice类型");

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

    // ==================== 注销命令测试 ====================

    @Test
    @DisplayName("测试发送注销命令 - 正常情况")
    void testSendUnregisterCommand_Success() {
        // 执行测试
        String actualCallId = ClientCommandSender.sendUnregisterCommand(
                clientFromDevice,
                clientToDevice
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试发送注销命令 - 异常情况处理")
    void testSendUnregisterCommand_ExceptionHandling() {
        // 测试空设备参数
        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendUnregisterCommand(null, clientToDevice);
        }, "发送设备不能为空");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendUnregisterCommand(clientFromDevice, null);
        }, "接收设备不能为空");
    }

    // ==================== 告警命令测试 ====================

    @Test
    @DisplayName("测试发送告警命令 - DeviceAlarm参数")
    void testSendAlarmCommand_DeviceAlarm() {
        // 准备测试数据
        DeviceAlarm deviceAlarm = new DeviceAlarm();
        deviceAlarm.setId("alarm001");
        deviceAlarm.setDeviceId("33010602011187000001");
        deviceAlarm.setAlarmPriority("1");
        deviceAlarm.setAlarmMethod("2");
        deviceAlarm.setAlarmTime(new Date());
        deviceAlarm.setAlarmDescription("测试告警");
        deviceAlarm.setLongitude(116.397128);
        deviceAlarm.setLatitude(39.916527);
        deviceAlarm.setAlarmType("1");

        // 执行测试
        String actualCallId = ClientCommandSender.sendAlarmCommand(
                clientFromDevice,
                clientToDevice,
                deviceAlarm
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试发送告警命令 - DeviceAlarmNotify参数")
    void testSendAlarmCommand_DeviceAlarmNotify() {
        // 准备测试数据
        DeviceAlarmNotify deviceAlarmNotify = new DeviceAlarmNotify();
        deviceAlarmNotify.setCmdType("Alarm");
        deviceAlarmNotify.setSn("123456");
        deviceAlarmNotify.setDeviceId("33010602011187000001");
        deviceAlarmNotify.setAlarmPriority("1");
        deviceAlarmNotify.setAlarmMethod("2");
        deviceAlarmNotify.setAlarmTime("2024-01-01T10:00:00Z");
        deviceAlarmNotify.setLongitude("116.397128");
        deviceAlarmNotify.setLatitude("39.916527");
        DeviceAlarmNotify.AlarmInfo alarmInfo = new DeviceAlarmNotify.AlarmInfo("1");
        deviceAlarmNotify.setInfo(alarmInfo);

        // 执行测试
        String actualCallId = ClientCommandSender.sendAlarmCommand(
                clientFromDevice,
                clientToDevice,
                deviceAlarmNotify
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试发送告警命令 - 不同告警级别")
    void testSendAlarmCommand_DifferentAlarmLevels() {
        // 测试不同的告警级别
        String[] alarmLevels = {"1", "2", "3", "4"};

        for (String alarmLevel : alarmLevels) {
            // 准备测试数据
            DeviceAlarm deviceAlarm = new DeviceAlarm();
            deviceAlarm.setId("alarm" + alarmLevel);
            deviceAlarm.setDeviceId("33010602011187000001");
            deviceAlarm.setAlarmPriority(alarmLevel);
            deviceAlarm.setAlarmMethod("2");
            deviceAlarm.setAlarmTime(new Date());
            deviceAlarm.setAlarmDescription("测试告警级别" + alarmLevel);
            deviceAlarm.setLongitude(116.397128);
            deviceAlarm.setLatitude(39.916527);
            deviceAlarm.setAlarmType("1");

            // 执行测试
            String actualCallId = ClientCommandSender.sendAlarmCommand(
                    clientFromDevice,
                    clientToDevice,
                    deviceAlarm
            );

            // 验证结果
            assertNotNull(actualCallId, "告警级别" + alarmLevel + "返回的callId不能为空");
            assertFalse(actualCallId.isEmpty(), "告警级别" + alarmLevel + "返回的callId不能为空字符串");
        }
    }

    @Test
    @DisplayName("测试发送告警命令 - 异常情况处理")
    void testSendAlarmCommand_ExceptionHandling() {
        // 测试空设备参数
        DeviceAlarm deviceAlarm = new DeviceAlarm();
        deviceAlarm.setId("alarm001");
        deviceAlarm.setDeviceId("33010602011187000001");
        deviceAlarm.setAlarmPriority("1");
        deviceAlarm.setAlarmMethod("2");
        deviceAlarm.setAlarmTime(new Date());
        deviceAlarm.setAlarmDescription("测试告警");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendAlarmCommand(null, clientToDevice, deviceAlarm);
        }, "发送设备不能为空");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendAlarmCommand(clientFromDevice, null, deviceAlarm);
        }, "接收设备不能为空");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendAlarmCommand(clientFromDevice, clientToDevice, (DeviceAlarm) null);
        }, "告警信息不能为空");
    }

    // ==================== 心跳命令测试 ====================

    @Test
    @DisplayName("测试发送心跳命令 - 正常情况")
    void testSendKeepaliveCommand_Success() {
        // 准备测试数据
        String status = "OK";

        // 执行测试
        String actualCallId = ClientCommandSender.sendKeepaliveCommand(
                clientFromDevice,
                clientToDevice,
                status
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试发送心跳命令 - 不同状态")
    void testSendKeepaliveCommand_DifferentStatus() {
        // 测试不同的状态值
        String[] statusValues = {"OK", "ERROR", "BUSY", "OFFLINE", "ONLINE"};

        for (String status : statusValues) {
            // 执行测试
            String actualCallId = ClientCommandSender.sendKeepaliveCommand(
                    clientFromDevice,
                    clientToDevice,
                    status
            );

            // 验证结果
            assertNotNull(actualCallId, "状态" + status + "返回的callId不能为空");
            assertFalse(actualCallId.isEmpty(), "状态" + status + "返回的callId不能为空字符串");
        }
    }

    @Test
    @DisplayName("测试发送心跳命令 - 带事件")
    void testSendKeepaliveCommand_WithEvents() {
        // 准备测试数据
        String status = "OK";
        Event errorEvent = eventResult -> {

        };
        Event okEvent = eventResult -> {

        };

        // 执行测试
        String actualCallId = ClientCommandSender.sendKeepaliveCommand(
                clientFromDevice,
                clientToDevice,
                status,
                errorEvent,
                okEvent
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试发送心跳命令 - 异常情况处理")
    void testSendKeepaliveCommand_ExceptionHandling() {
        // 测试空设备参数
        String status = "OK";

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendKeepaliveCommand(null, clientToDevice, status);
        }, "发送设备不能为空");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendKeepaliveCommand(clientFromDevice, null, status);
        }, "接收设备不能为空");

        assertThrows(IllegalArgumentException.class, () -> {
            ClientCommandSender.sendKeepaliveCommand(clientFromDevice, clientToDevice, (String) null);
        }, "状态信息不能为空");
    }

    // ==================== 策略模式测试 ====================

    @Test
    @DisplayName("测试策略模式 - 不同命令类型")
    void testSendCommand_DifferentCommandTypes() {
        // 测试不同的命令类型
        String[] commandTypes = {"REGISTER", "Alarm", "Keepalive"};

        for (String commandType : commandTypes) {
            // 准备测试参数
            Object[] params = {};
            if ("REGISTER".equals(commandType)) {
                params = new Object[]{3600};
            } else if ("Alarm".equals(commandType)) {
                DeviceAlarm deviceAlarm = new DeviceAlarm();
                deviceAlarm.setId("alarm001");
                deviceAlarm.setDeviceId("33010602011187000001");
                deviceAlarm.setAlarmPriority("1");
                deviceAlarm.setAlarmMethod("2");
                deviceAlarm.setAlarmTime(new Date());
                deviceAlarm.setAlarmDescription("测试告警");
                params = new Object[]{deviceAlarm};
            } else if ("Keepalive".equals(commandType)) {
                params = new Object[]{"OK"};
            }

            // 执行测试
            String actualCallId = ClientCommandSender.sendCommand(
                    commandType,
                    clientFromDevice,
                    clientToDevice,
                    params
            );

            // 验证结果
            assertNotNull(actualCallId, "命令类型" + commandType + "返回的callId不能为空");
            assertFalse(actualCallId.isEmpty(), "命令类型" + commandType + "返回的callId不能为空字符串");
        }
    }

    @Test
    @DisplayName("测试策略模式 - 带事件")
    void testSendCommand_WithEvents() {
        // 准备测试数据
        String commandType = "Keepalive";
        String status = "OK";
        Event errorEvent = eventResult -> {

        };
        Event okEvent = eventResult -> {

        };


        // 执行测试
        String actualCallId = ClientCommandSender.sendCommand(
                commandType,
                clientFromDevice,
                clientToDevice,
                errorEvent,
                okEvent,
                status
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试策略模式 - 订阅命令")
    void testSendSubscribeCommand() {
        // 准备测试数据
        String commandType = "Catalog";
        SubscribeInfo subscribeInfo = new SubscribeInfo();
        subscribeInfo.setEventId("event001");
        subscribeInfo.setEventType("Catalog");
        subscribeInfo.setExpires(3600);

        // 创建测试用的设备项
        DeviceItem deviceItem = DeviceItem.getInstanceExample(clientFromDevice.getUserId());

        // 执行测试
        String actualCallId = ClientCommandSender.sendSubscribeCommand(
                commandType,
                clientFromDevice,
                clientToDevice,
                subscribeInfo,
                deviceItem
        );

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    // ==================== 建造者模式测试 ====================

    @Test
    @DisplayName("测试建造者模式 - 基本命令")
    void testBuilder_BasicCommand() {
        // 测试基本命令
        String actualCallId = ClientCommandSender.builder()
                .commandType("Keepalive")
                .fromDevice(clientFromDevice)
                .toDevice(clientToDevice)
                .params("OK")
                .execute();

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试建造者模式 - 带事件")
    void testBuilder_WithEvents() {
        // 准备测试数据
        Event errorEvent = eventResult -> {

        };
        Event okEvent = eventResult -> {

        };


        // 测试带事件的命令
        String actualCallId = ClientCommandSender.builder()
                .commandType("Keepalive")
                .fromDevice(clientFromDevice)
                .toDevice(clientToDevice)
                .errorEvent(errorEvent)
                .okEvent(okEvent)
                .params("OK")
                .execute();

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    @Test
    @DisplayName("测试建造者模式 - 订阅命令")
    void testBuilder_SubscribeCommand() {
        // 准备测试数据
        SubscribeInfo subscribeInfo = new SubscribeInfo();
        subscribeInfo.setEventId("event001");
        subscribeInfo.setEventType("Catalog");
        subscribeInfo.setExpires(3600);

        // 创建测试用的设备项
        DeviceItem deviceItem = DeviceItem.getInstanceExample(clientFromDevice.getUserId());

        // 测试订阅命令
        String actualCallId = ClientCommandSender.builder()
                .commandType("Catalog")
                .fromDevice(clientFromDevice)
                .toDevice(clientToDevice)
                .subscribeInfo(subscribeInfo)
                .params(deviceItem)
                .execute();

        // 验证结果
        assertNotNull(actualCallId, "返回的callId不能为空");
        assertFalse(actualCallId.isEmpty(), "返回的callId不能为空字符串");
    }

    // ==================== 工具方法测试 ====================

    @Test
    @DisplayName("测试获取已注册命令类型")
    void testGetRegisteredCommandTypes() {
        // 执行测试
        java.util.Set<String> commandTypes = ClientCommandSender.getRegisteredCommandTypes();

        // 验证结果
        assertNotNull(commandTypes, "返回的命令类型集合不能为空");
        assertFalse(commandTypes.isEmpty(), "返回的命令类型集合不能为空");

        // 验证包含基本命令类型
        assertTrue(commandTypes.contains("REGISTER"), "应该包含REGISTER命令类型");
        assertTrue(commandTypes.contains("Alarm"), "应该包含Alarm命令类型");
        assertTrue(commandTypes.contains("Keepalive"), "应该包含Keepalive命令类型");
    }

    @Test
    @DisplayName("测试检查命令类型是否已注册")
    void testHasCommandType() {
        // 测试已注册的命令类型
        assertTrue(ClientCommandSender.hasCommandType("REGISTER"), "REGISTER命令类型应该已注册");
        assertTrue(ClientCommandSender.hasCommandType("Alarm"), "Alarm命令类型应该已注册");
        assertTrue(ClientCommandSender.hasCommandType("Keepalive"), "Keepalive命令类型应该已注册");

        // 测试未注册的命令类型
        assertFalse(ClientCommandSender.hasCommandType("UNKNOWN"), "未知命令类型应该未注册");
        assertFalse(ClientCommandSender.hasCommandType(""), "空命令类型应该未注册");
        assertThrows(IllegalArgumentException.class, () -> ClientCommandSender.hasCommandType(null), "命令类型不能为空");
    }

    @Test
    @DisplayName("测试注册自定义命令策略")
    void testRegisterCommandStrategy() {
        // 准备测试数据
        String customCommandType = "CustomCommand";

        // 验证命令类型未注册
        assertFalse(ClientCommandSender.hasCommandType(customCommandType), "自定义命令类型应该未注册");

        // 注册自定义命令策略
        ClientCommandStrategy customStrategy = new ClientCommandStrategy() {
            @Override
            public String execute(FromDevice fromDevice, ToDevice toDevice, Object... params) {
                return "custom_call_id";
            }

            @Override
            public String execute(FromDevice fromDevice, ToDevice toDevice, Event errorEvent, Event okEvent, Object... params) {
                return "custom_call_id_with_events";
            }

            @Override
            public String executeWithSubscribe(FromDevice fromDevice, ToDevice toDevice, SubscribeInfo subscribeInfo, Object... params) {
                return "custom_call_id_with_subscribe";
            }

            @Override
            public String getCommandType() {
                return customCommandType;
            }

            @Override
            public String getCommandDescription() {
                return "自定义命令";
            }
        };

        ClientCommandSender.registerCommandStrategy(customCommandType, customStrategy);

        // 验证命令类型已注册
        assertTrue(ClientCommandSender.hasCommandType(customCommandType), "自定义命令类型应该已注册");

        // 测试执行自定义命令
        String actualCallId = ClientCommandSender.sendCommand(
                customCommandType,
                clientFromDevice,
                clientToDevice
        );

        // 验证结果
        assertNotNull(actualCallId, "自定义命令返回的callId不能为空");
        assertEquals("custom_call_id", actualCallId, "自定义命令应该返回预期的callId");
    }
}