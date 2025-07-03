package io.github.lunasaw.gbproxy.test;

import io.github.lunasaw.gb28181.common.entity.DeviceAlarm;
import io.github.lunasaw.gb28181.common.entity.control.DragZoom;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MediaStatusNotify;
import io.github.lunasaw.gb28181.common.entity.notify.MobilePositionNotify;
import io.github.lunasaw.gb28181.common.entity.query.DeviceAlarmQuery;
import io.github.lunasaw.gb28181.common.entity.query.DeviceRecordQuery;
import io.github.lunasaw.gb28181.common.entity.response.*;
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

import com.luna.common.date.DateUtils;

/**
 * GB28181消息处理综合测试
 * 涵盖客户端和服务器端的不同消息处理场景
 * 
 * @author luna
 * @date 2023/10/30
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageProcessingTest {

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

    @BeforeEach
    public void setUp() {
        log.info("初始化SIP端口监听");
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, clientFromDevice.getPort(), true);
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, serverFromDevice.getPort(), true);
        
        // 将设备加入配置映射
        DeviceConfig.DEVICE_CLIENT_VIEW_MAP.put(clientToDevice.getUserId(), clientToDevice);
        DeviceConfig.DEVICE_SERVER_VIEW_MAP.put(serverToDevice.getUserId(), serverToDevice);
    }

    /**
     * 测试客户端注册消息处理
     */
    @Test
    @Order(1)
    public void testClientRegisterMessage() {
        log.info("测试客户端注册消息处理");
        
        // 设备注册
        String callId = ClientSendCmd.deviceRegister(
            (FromDevice) clientFromDevice, 
            (ToDevice) clientToDevice, 
            3600,
            new Event() {
                @Override
                public void response(EventResult eventResult) {
                    log.info("注册响应: {}", eventResult);
                    Assertions.assertNotNull(eventResult);
                }
            }
        );
        
        Assertions.assertNotNull(callId);
        log.info("客户端注册请求发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端心跳消息处理
     */
    @Test
    @Order(2)
    public void testClientKeepAliveMessage() {
        log.info("测试客户端心跳消息处理");
        
        String callId = ClientSendCmd.deviceKeepLiveNotify(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            "OK",
            new Event() {
                @Override
                public void response(EventResult eventResult) {
                    log.info("心跳响应: {}", eventResult);
                }
            }
        );
        
        Assertions.assertNotNull(callId);
        log.info("客户端心跳消息发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端告警通知消息处理
     */
    @Test
    @Order(3)
    public void testClientAlarmNotifyMessage() {
        log.info("测试客户端告警通知消息处理");
        
        DeviceAlarm deviceAlarm = new DeviceAlarm();
        deviceAlarm.setAlarmPriority("3");
        deviceAlarm.setAlarmMethod("1");
        deviceAlarm.setAlarmTime(new Date());
        deviceAlarm.setAlarmDescription("测试告警");
        deviceAlarm.setLongitude(116.397128);
        deviceAlarm.setLatitude(39.916527);
        
        String callId = ClientSendCmd.deviceAlarmNotify(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceAlarm
        );
        
        Assertions.assertNotNull(callId);
        log.info("客户端告警通知发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端设备信息响应
     */
    @Test
    @Order(4)
    public void testClientDeviceInfoResponse() {
        log.info("测试客户端设备信息响应");
        
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("测试设备");
        deviceInfo.setManufacturer("测试厂商");
        deviceInfo.setModel("TestModel");
        deviceInfo.setFirmware("1.0.0");
        deviceInfo.setChannel(20);
        
        String callId = ClientSendCmd.deviceInfoResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceInfo
        );
        
        Assertions.assertNotNull(callId);
        log.info("客户端设备信息响应发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端通道目录响应
     */
    @Test
    @Order(5)
    public void testClientCatalogResponse() {
        log.info("测试客户端通道目录响应");
        
        List<DeviceItem> deviceItems = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            DeviceItem item = new DeviceItem();
            item.setDeviceId(clientFromDevice.getUserId() + String.format("%02d", i));
            item.setName("测试通道" + i);
            item.setManufacturer("测试厂商");
            item.setModel("TestChannel");
            item.setOwner("Owner");
            item.setCivilCode("410100");
            item.setAddress("测试地址" + i);
            item.setParental(0);
            item.setParentId(clientFromDevice.getUserId());
            item.setSafetyWay(0);
            item.setRegisterWay(1);
            item.setSecrecy(0);
            item.setStatus("ON");
            deviceItems.add(item);
        }
        
        String callId = ClientSendCmd.deviceChannelCatalogResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceItems
        );
        
        Assertions.assertNotNull(callId);
        log.info("客户端通道目录响应发送成功，callId: {}, 通道数量: {}", callId, deviceItems.size());
    }

    /**
     * 测试服务器端设备信息查询
     */
    @Test
    @Order(6)
    public void testServerDeviceInfoQuery() {
        log.info("测试服务器端设备信息查询");
        
        String callId = ServerSendCmd.deviceInfo(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );
        
        Assertions.assertNotNull(callId);
        log.info("服务器端设备信息查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端通道目录查询
     */
    @Test
    @Order(7)
    public void testServerCatalogQuery() {
        log.info("测试服务器端通道目录查询");
        
        String callId = ServerSendCmd.deviceCatalogQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );
        
        Assertions.assertNotNull(callId);
        log.info("服务器端通道目录查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端设备状态查询
     */
    @Test
    @Order(8)
    public void testServerDeviceStatusQuery() {
        log.info("测试服务器端设备状态查询");
        
        String callId = ServerSendCmd.deviceStatusQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );
        
        Assertions.assertNotNull(callId);
        log.info("服务器端设备状态查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端录像信息查询
     */
    @Test
    @Order(9)
    public void testServerRecordInfoQuery() {
        log.info("测试服务器端录像信息查询");
        
        Date startTime = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24小时前
        Date endTime = new Date(); // 现在
        
        String callId = ServerSendCmd.deviceRecordInfoQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            startTime,
            endTime
        );
        
        Assertions.assertNotNull(callId);
        log.info("服务器端录像信息查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端PTZ控制
     */
    @Test
    @Order(10)
    public void testServerPtzControl() {
        log.info("测试服务器端PTZ控制");
        
        // 测试云台向上移动
        String callId1 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.UP,
            50
        );
        
        Assertions.assertNotNull(callId1);
        log.info("PTZ向上控制发送成功，callId: {}", callId1);
        
        // 测试云台停止
        String callId2 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.DOWN,
            0
        );
        
        Assertions.assertNotNull(callId2);
        log.info("PTZ停止控制发送成功，callId: {}", callId2);
    }

    /**
     * 测试服务器端拉框放大控制
     */
    @Test
    @Order(11)
    public void testServerDragZoomControl() {
        log.info("测试服务器端拉框放大控制");
        
        DragZoom dragZoom = new DragZoom();
        dragZoom.setLength("200");
        dragZoom.setWidth("150");
        dragZoom.setMidPointX("100");
        dragZoom.setMidPointY("100");
        dragZoom.setLengthX("50");
        dragZoom.setLengthY("50");
        
        String callId = ServerSendCmd.deviceControlDragIn(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            dragZoom
        );
        
        Assertions.assertNotNull(callId);
        log.info("拉框放大控制发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端实时流邀请
     */
    @Test
    @Order(12)
    public void testServerInvitePlay() {
        log.info("测试服务器端实时流邀请");
        
        String callId = ServerSendCmd.deviceInvitePlay(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "192.168.1.100",
            10000
        );
        
        Assertions.assertNotNull(callId);
        log.info("实时流邀请发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端录像回放邀请
     */
    @Test
    @Order(13)
    public void testServerInvitePlayback() {
        log.info("测试服务器端录像回放邀请");
        
        Date startTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000); // 1小时前
        Date endTime = new Date();
        
        String callId = ServerSendCmd.deviceInvitePlayBack(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "192.168.1.100",
            10001,
            startTime,
            endTime
        );
        
        Assertions.assertNotNull(callId);
        log.info("录像回放邀请发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端录像信息响应
     */
    @Test
    @Order(14)
    public void testClientRecordResponse() {
        log.info("测试客户端录像信息响应");
        
        List<DeviceRecord.RecordItem> recordItems = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            DeviceRecord.RecordItem item = new DeviceRecord.RecordItem();
            item.setDeviceId(clientFromDevice.getUserId());
            item.setName("录像文件" + i);
            item.setFilePath("/record/file" + i + ".mp4");
            item.setStartTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, new Date(System.currentTimeMillis() - i * 60 * 60 * 1000)));
            item.setEndTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, new Date(System.currentTimeMillis() - (i-1) * 60 * 60 * 1000)));
            item.setSecrecy("0");
            item.setType("time");
            recordItems.add(item);
        }
        
        ClientSendCmd.deviceRecordResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            recordItems,
            "test-sn-001"
        );
        
        log.info("客户端录像信息响应发送成功，录像数量: {}", recordItems.size());
    }

    /**
     * 测试客户端设备状态响应
     */
    @Test
    @Order(15)
    public void testClientDeviceStatusResponse() {
        log.info("测试客户端设备状态响应");
        
        String callId = ClientSendCmd.deviceStatusResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            "ONLINE"
        );
        
        Assertions.assertNotNull(callId);
        log.info("客户端设备状态响应发送成功，callId: {}", callId);
    }

    /**
     * 测试错误场景处理
     */
    @Test
    @Order(16)
    public void testErrorScenarios() {
        log.info("测试错误场景处理");
        
        // 测试空设备信息
        Assertions.assertThrows(Exception.class, () -> {
            ClientSendCmd.deviceInfoResponse(
                (FromDevice) clientFromDevice,
                (ToDevice) clientToDevice,
                null
            );
        });
        
        log.info("错误场景测试完成");
    }

    @AfterEach
    public void tearDown() {
        log.info("清理测试环境");
        // 可以在这里添加清理逻辑
    }
}