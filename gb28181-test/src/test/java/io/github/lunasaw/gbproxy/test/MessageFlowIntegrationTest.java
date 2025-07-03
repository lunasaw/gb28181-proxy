package io.github.lunasaw.gbproxy.test;

import io.github.lunasaw.gb28181.common.entity.response.DeviceInfo;
import io.github.lunasaw.gb28181.common.entity.response.DeviceItem;
import io.github.lunasaw.gb28181.common.entity.response.DeviceRecord;
import io.github.lunasaw.gb28181.common.entity.utils.PtzCmdEnum;
import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.server.transimit.cmd.ServerSendCmd;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * GB28181消息流集成测试
 * 模拟完整的客户端-服务器交互场景
 * 
 * @author luna
 * @date 2023/10/30
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageFlowIntegrationTest {

    @Autowired
    @Qualifier("clientFrom")
    private Device clientFromDevice;

    @Autowired
    @Qualifier("clientTo")
    private Device clientToDevice;

    @Autowired
    @Qualifier("serverFrom")
    private Device serverFromDevice;

    @Autowired
    @Qualifier("serverTo")
    private Device serverToDevice;

    @Autowired
    private SipLayer sipLayer;

    private CountDownLatch responseReceived;

    @BeforeEach
    public void setUp() {
        log.info("初始化消息流集成测试环境");
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, clientFromDevice.getPort(), true);
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, serverFromDevice.getPort(), true);
        
        DeviceConfig.DEVICE_CLIENT_VIEW_MAP.put(clientToDevice.getUserId(), clientToDevice);
        DeviceConfig.DEVICE_SERVER_VIEW_MAP.put(serverToDevice.getUserId(), serverToDevice);
        
        responseReceived = new CountDownLatch(1);
    }

    /**
     * 测试完整的设备注册流程
     */
    @Test
    @Order(1)
    public void testCompleteRegistrationFlow() throws InterruptedException {
        log.info("测试完整的设备注册流程");

        // 1. 客户端发起注册
        String registrationCallId = ClientSendCmd.deviceRegister(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            3600,
            new Event() {
                @Override
                public void response(EventResult eventResult) {
                    log.info("注册响应接收: 状态={}, 消息={}", eventResult.getType(), eventResult.getMsg());
                    if (eventResult.getStatusCode() == 200) {
                        // 2. 注册成功后发送心跳
                        ClientSendCmd.deviceKeepLiveNotify(
                            (FromDevice) clientFromDevice,
                            (ToDevice) clientToDevice,
                            "OK"
                        );
                        log.info("注册成功，发送心跳");
                    }
                    responseReceived.countDown();
                }
            }
        );

        Assertions.assertNotNull(registrationCallId);
        
        // 等待响应
        boolean received = responseReceived.await(5, TimeUnit.SECONDS);
        Assertions.assertTrue(received, "注册响应应该在5秒内收到");
        
        log.info("设备注册流程测试完成");
    }

    /**
     * 测试设备查询-响应流程
     */
    @Test
    @Order(2)
    public void testQueryResponseFlow() {
        log.info("测试设备查询-响应流程");

        // 1. 服务器发起设备信息查询
        String queryCallId = ServerSendCmd.deviceInfo(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );
        Assertions.assertNotNull(queryCallId);
        log.info("服务器发送设备信息查询，callId: {}", queryCallId);

        // 2. 模拟客户端收到查询后的响应
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("集成测试设备");
        deviceInfo.setManufacturer("测试制造商");
        deviceInfo.setModel("TestModel");
        deviceInfo.setFirmware("3.0.0");
        deviceInfo.setChannel(16);
        deviceInfo.setResult("OK");

        String responseCallId = ClientSendCmd.deviceInfoResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceInfo
        );
        Assertions.assertNotNull(responseCallId);
        log.info("客户端响应设备信息，callId: {}", responseCallId);
    }

    /**
     * 测试通道目录查询-响应流程
     */
    @Test
    @Order(3)
    public void testCatalogQueryResponseFlow() {
        log.info("测试通道目录查询-响应流程");

        // 1. 服务器发起通道目录查询
        String catalogQueryCallId = ServerSendCmd.deviceCatalogQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );
        Assertions.assertNotNull(catalogQueryCallId);
        log.info("服务器发送通道目录查询，callId: {}", catalogQueryCallId);

        // 2. 客户端响应通道目录
        List<DeviceItem> deviceItems = createTestDeviceItems(8);
        String catalogResponseCallId = ClientSendCmd.deviceChannelCatalogResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceItems
        );
        Assertions.assertNotNull(catalogResponseCallId);
        log.info("客户端响应通道目录，通道数: {}, callId: {}", deviceItems.size(), catalogResponseCallId);
    }

    /**
     * 测试录像查询-响应流程
     */
    @Test
    @Order(4)
    public void testRecordQueryResponseFlow() {
        log.info("测试录像查询-响应流程");

        // 1. 服务器发起录像查询
        Date startTime = new Date(System.currentTimeMillis() - 12 * 60 * 60 * 1000); // 12小时前
        Date endTime = new Date();

        String recordQueryCallId = ServerSendCmd.deviceRecordInfoQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            startTime,
            endTime
        );
        Assertions.assertNotNull(recordQueryCallId);
        log.info("服务器发送录像查询，callId: {}", recordQueryCallId);

        // 2. 客户端响应录像列表
        List<DeviceRecord.RecordItem> recordItems = createTestRecordItems(6);
        ClientSendCmd.deviceRecordResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            recordItems,
            "record-flow-test-001"
        );
        log.info("客户端响应录像列表，录像数: {}", recordItems.size());
    }

    /**
     * 测试PTZ控制流程
     */
    @Test
    @Order(5)
    public void testPtzControlFlow() {
        log.info("测试PTZ控制流程");

        // 模拟一系列PTZ控制命令
        List<PtzCmdEnum> ptzCommands = List.of(
            PtzCmdEnum.UP,
            PtzCmdEnum.DOWN,
            PtzCmdEnum.LEFT,
            PtzCmdEnum.RIGHT,
            PtzCmdEnum.ZOOMIN,
            PtzCmdEnum.ZOOMOUT
        );

        for (PtzCmdEnum cmd : ptzCommands) {
            String callId = ServerSendCmd.deviceControlPtzCmd(
                (FromDevice) serverFromDevice,
                (ToDevice) serverToDevice,
                cmd,
                80
            );
            Assertions.assertNotNull(callId);
            log.info("发送PTZ控制命令: {}, callId: {}", cmd.getCommand(), callId);
        }

        log.info("PTZ控制流程测试完成");
    }

    /**
     * 测试实时流邀请流程
     */
    @Test
    @Order(6)
    public void testInviteFlow() {
        log.info("测试实时流邀请流程");

        // 1. 服务器发起实时流邀请
        String inviteCallId = ServerSendCmd.deviceInvitePlay(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "192.168.1.100",
            10000
        );
        Assertions.assertNotNull(inviteCallId);
        log.info("服务器发送实时流邀请，callId: {}", inviteCallId);

        // 2. 模拟客户端ACK响应
        String ackCallId = ClientSendCmd.deviceAck(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice
        );
        Assertions.assertNotNull(ackCallId);
        log.info("客户端发送ACK响应，callId: {}", ackCallId);
    }

    /**
     * 测试录像回放邀请流程
     */
    @Test
    @Order(7)
    public void testPlaybackInviteFlow() {
        log.info("测试录像回放邀请流程");

        // 1. 服务器发起录像回放邀请
        Date startTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000); // 1小时前
        Date endTime = new Date(System.currentTimeMillis() - 30 * 60 * 1000);   // 30分钟前

        String playbackCallId = ServerSendCmd.deviceInvitePlayBack(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "192.168.1.100",
            10001,
            startTime,
            endTime
        );
        Assertions.assertNotNull(playbackCallId);
        log.info("服务器发送录像回放邀请，callId: {}", playbackCallId);

        // 2. 模拟客户端ACK响应
        String ackCallId = ClientSendCmd.deviceAck(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            playbackCallId
        );
        Assertions.assertNotNull(ackCallId);
        log.info("客户端发送ACK响应，callId: {}", ackCallId);
    }

    /**
     * 测试订阅流程
     */
    @Test
    @Order(8)
    public void testSubscriptionFlow() {
        log.info("测试订阅流程");

        // 1. 服务器发起通道目录订阅
        String catalogSubCallId = ServerSendCmd.deviceCatalogSubscribe(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            1800, // 30分钟
            "Catalog"
        );
        Assertions.assertNotNull(catalogSubCallId);
        log.info("服务器发送通道目录订阅，callId: {}", catalogSubCallId);

        // 2. 模拟客户端发送订阅更新通知
        List<io.github.lunasaw.gb28181.common.entity.notify.DeviceUpdateItem> updateItems = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            io.github.lunasaw.gb28181.common.entity.notify.DeviceUpdateItem item = 
                new io.github.lunasaw.gb28181.common.entity.notify.DeviceUpdateItem();
            item.setDeviceId(clientFromDevice.getUserId() + String.format("%03d", i));
            item.setEvent("ON");
            updateItems.add(item);
        }

        String updateCallId = ClientSendCmd.deviceChannelUpdateCatlog(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            updateItems,
            null
        );
        Assertions.assertNotNull(updateCallId);
        log.info("客户端发送通道更新通知，callId: {}", updateCallId);
    }

    /**
     * 测试告警流程
     */
    @Test
    @Order(9)
    public void testAlarmFlow() {
        log.info("测试告警流程");

        // 1. 客户端发送告警通知
        io.github.lunasaw.gb28181.common.entity.DeviceAlarm deviceAlarm = 
            new io.github.lunasaw.gb28181.common.entity.DeviceAlarm();
        deviceAlarm.setAlarmPriority("2");
        deviceAlarm.setAlarmMethod("1");
        deviceAlarm.setAlarmTime(new Date());
        deviceAlarm.setAlarmDescription("集成测试告警");
        deviceAlarm.setLongitude(116.397128);
        deviceAlarm.setLatitude(39.916527);

        String alarmCallId = ClientSendCmd.deviceAlarmNotify(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceAlarm
        );
        Assertions.assertNotNull(alarmCallId);
        log.info("客户端发送告警通知，callId: {}", alarmCallId);

        // 2. 服务器发送告警复位命令
        String resetCallId = ServerSendCmd.deviceControlAlarm(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "1", // 报警方式
            "1"  // 报警类型
        );
        Assertions.assertNotNull(resetCallId);
        log.info("服务器发送告警复位命令，callId: {}", resetCallId);
    }

    /**
     * 测试会话结束流程
     */
    @Test
    @Order(10)
    public void testSessionTerminationFlow() {
        log.info("测试会话结束流程");

        // 1. 客户端发送BYE结束会话
        String byeCallId = ClientSendCmd.deviceBye(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice
        );
        Assertions.assertNotNull(byeCallId);
        log.info("客户端发送BYE，callId: {}", byeCallId);

        // 2. 客户端注销设备
        String unregisterCallId = ClientSendCmd.deviceUnRegister(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice
        );
        Assertions.assertNotNull(unregisterCallId);
        log.info("客户端注销设备，callId: {}", unregisterCallId);
    }

    /**
     * 创建测试用的设备通道项
     */
    private List<DeviceItem> createTestDeviceItems(int count) {
        List<DeviceItem> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            DeviceItem item = new DeviceItem();
            item.setDeviceId(clientFromDevice.getUserId() + String.format("%03d", i));
            item.setName("集成测试通道" + i);
            item.setManufacturer("集成测试厂商");
            item.setModel("IntegrationTest");
            item.setOwner("TestOwner");
            item.setCivilCode("410100");
            item.setAddress("集成测试地址" + i);
            item.setParental(0);
            item.setParentId(clientFromDevice.getUserId());
            item.setSafetyWay(0);
            item.setRegisterWay(1);
            item.setSecrecy(0);
            item.setStatus("ON");
            items.add(item);
        }
        return items;
    }

    /**
     * 创建测试用的录像项
     */
    private List<DeviceRecord.RecordItem> createTestRecordItems(int count) {
        List<DeviceRecord.RecordItem> items = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            DeviceRecord.RecordItem item = new DeviceRecord.RecordItem();
            item.setDeviceId(clientFromDevice.getUserId());
            item.setName("集成测试录像" + i);
            item.setFilePath("/integration/test/record" + i + ".mp4");
            item.setStartTime("2023-11-01T" + String.format("%02d", 8 + i) + ":00:00");
            item.setEndTime("2023-11-01T" + String.format("%02d", 9 + i) + ":00:00");
            item.setSecrecy("0");
            item.setType("time");
            item.setFileSize(String.valueOf(1024 * 1024 * 100)); // 100MB
            items.add(item);
        }
        return items;
    }

    @AfterEach
    public void tearDown() {
        log.info("清理消息流集成测试环境");
    }
}