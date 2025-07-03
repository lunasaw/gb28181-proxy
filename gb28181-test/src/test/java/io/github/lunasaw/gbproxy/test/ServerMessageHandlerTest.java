package io.github.lunasaw.gbproxy.test;

import io.github.lunasaw.gb28181.common.entity.utils.PtzCmdEnum;
import io.github.lunasaw.gb28181.common.entity.control.DragZoom;
import io.github.lunasaw.gb28181.common.entity.control.DeviceControlPosition;
import io.github.lunasaw.gbproxy.server.transimit.cmd.ServerSendCmd;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * 服务器端消息处理器专门测试
 * 测试服务器端向客户端发送的各种查询和控制命令
 * 
 * @author luna
 * @date 2023/10/30
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerMessageHandlerTest {

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
        log.info("初始化服务器端测试环境");
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, serverFromDevice.getPort(), true);
        DeviceConfig.DEVICE_SERVER_VIEW_MAP.put(serverToDevice.getUserId(), serverToDevice);
    }

    /**
     * 测试服务器端发送设备信息查询
     */
    @Test
    @Order(1)
    public void testSendDeviceInfoQuery() {
        log.info("测试服务器端发送设备信息查询");

        String callId = ServerSendCmd.deviceInfo(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );

        Assertions.assertNotNull(callId);
        log.info("设备信息查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端发送设备状态查询
     */
    @Test
    @Order(2)
    public void testSendDeviceStatusQuery() {
        log.info("测试服务器端发送设备状态查询");

        String callId = ServerSendCmd.deviceStatusQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );

        Assertions.assertNotNull(callId);
        log.info("设备状态查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端发送通道目录查询
     */
    @Test
    @Order(3)
    public void testSendCatalogQuery() {
        log.info("测试服务器端发送通道目录查询");

        String callId = ServerSendCmd.deviceCatalogQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );

        Assertions.assertNotNull(callId);
        log.info("通道目录查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端发送录像信息查询（时间范围）
     */
    @Test
    @Order(4)
    public void testSendRecordInfoQuery() {
        log.info("测试服务器端发送录像信息查询");

        Date startTime = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000); // 24小时前
        Date endTime = new Date();

        String callId = ServerSendCmd.deviceRecordInfoQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            startTime,
            endTime,
            "0", // 不涉密
            "all" // 所有类型
        );

        Assertions.assertNotNull(callId);
        log.info("录像信息查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端发送告警查询
     */
    @Test
    @Order(5)
    public void testSendAlarmQuery() {
        log.info("测试服务器端发送告警查询");

        Date startTime = new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000); // 7天前
        Date endTime = new Date();

        String callId = ServerSendCmd.deviceAlarmQuery(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            startTime,
            endTime,
            "1", // 起始级别
            "4", // 结束级别
            "1", // 报警方式
            "1"  // 报警类型
        );

        Assertions.assertNotNull(callId);
        log.info("告警查询发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端PTZ云台控制
     */
    @Test
    @Order(6)
    public void testPtzControls() {
        log.info("测试服务器端PTZ云台控制");

        // 测试向上移动
        String callId1 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.UP,
            100
        );
        Assertions.assertNotNull(callId1);
        log.info("PTZ向上控制发送成功，callId: {}", callId1);

        // 测试向下移动
        String callId2 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.DOWN,
            80
        );
        Assertions.assertNotNull(callId2);
        log.info("PTZ向下控制发送成功，callId: {}", callId2);

        // 测试向左移动
        String callId3 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.LEFT,
            60
        );
        Assertions.assertNotNull(callId3);
        log.info("PTZ向左控制发送成功，callId: {}", callId3);

        // 测试向右移动
        String callId4 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.RIGHT,
            60
        );
        Assertions.assertNotNull(callId4);
        log.info("PTZ向右控制发送成功，callId: {}", callId4);

        // 测试放大
        String callId5 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.ZOOMIN,
            40
        );
        Assertions.assertNotNull(callId5);
        log.info("PTZ放大控制发送成功，callId: {}", callId5);

        // 测试缩小
        String callId6 = ServerSendCmd.deviceControlPtzCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            PtzCmdEnum.ZOOMOUT,
            40
        );
        Assertions.assertNotNull(callId6);
        log.info("PTZ缩小控制发送成功，callId: {}", callId6);
    }

    /**
     * 测试服务器端拉框放大/缩小控制
     */
    @Test
    @Order(7)
    public void testDragZoomControls() {
        log.info("测试服务器端拉框放大/缩小控制");

        DragZoom dragZoom = new DragZoom();
        dragZoom.setLength("400");
        dragZoom.setWidth("300");
        dragZoom.setMidPointX("200");
        dragZoom.setMidPointY("150");
        dragZoom.setLengthX("100");
        dragZoom.setLengthY("75");

        // 测试拉框放大
        String callId1 = ServerSendCmd.deviceControlDragIn(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            dragZoom
        );
        Assertions.assertNotNull(callId1);
        log.info("拉框放大控制发送成功，callId: {}", callId1);

        // 测试拉框缩小
        String callId2 = ServerSendCmd.deviceControlDragOut(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            dragZoom
        );
        Assertions.assertNotNull(callId2);
        log.info("拉框缩小控制发送成功，callId: {}", callId2);
    }

    /**
     * 测试服务器端设备配置控制
     */
    @Test
    @Order(8)
    public void testDeviceConfig() {
        log.info("测试服务器端设备配置控制");

        String callId = ServerSendCmd.deviceConfig(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "更新后的设备名称",
            "7200", // 注册过期时间
            "30",   // 心跳间隔
            "5"     // 心跳超时次数
        );

        Assertions.assertNotNull(callId);
        log.info("设备配置控制发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端配置下载
     */
    @Test
    @Order(9)
    public void testConfigDownload() {
        log.info("测试服务器端配置下载");

        String callId = ServerSendCmd.deviceConfigDownload(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "BasicParam" // 配置类型
        );

        Assertions.assertNotNull(callId);
        log.info("配置下载请求发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端强制关键帧
     */
    @Test
    @Order(10)
    public void testForceIFrame() {
        log.info("测试服务器端强制关键帧");

        String callId = ServerSendCmd.deviceControlIdr(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "Send"
        );

        Assertions.assertNotNull(callId);
        log.info("强制关键帧控制发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端看守位控制
     */
    @Test
    @Order(11)
    public void testHomePositionControl() {
        log.info("测试服务器端看守位控制");

        String callId = ServerSendCmd.deviceControlAlarm(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "1",   // 开启看守位
            "300", // 300秒后自动归位
            "1"    // 预置位编号1
        );

        Assertions.assertNotNull(callId);
        log.info("看守位控制发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端报警布防/撤防
     */
    @Test
    @Order(12)
    public void testGuardControl() {
        log.info("测试服务器端报警布防/撤防");

        // 测试布防
        String callId1 = ServerSendCmd.deviceControlGuardCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "SetGuard"
        );
        Assertions.assertNotNull(callId1);
        log.info("报警布防控制发送成功，callId: {}", callId1);

        // 测试撤防
        String callId2 = ServerSendCmd.deviceControlGuardCmd(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "ResetGuard"
        );
        Assertions.assertNotNull(callId2);
        log.info("报警撤防控制发送成功，callId: {}", callId2);
    }

    /**
     * 测试服务器端设备重启
     */
    @Test
    @Order(13)
    public void testDeviceReboot() {
        log.info("测试服务器端设备重启");

        String callId = ServerSendCmd.deviceControlTeleBoot(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice
        );

        Assertions.assertNotNull(callId);
        log.info("设备重启控制发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端录像控制
     */
    @Test
    @Order(14)
    public void testRecordControl() {
        log.info("测试服务器端录像控制");

        // 开始录像
        String callId1 = ServerSendCmd.deviceControlTeleBoot(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "Record"
        );
        Assertions.assertNotNull(callId1);
        log.info("开始录像控制发送成功，callId: {}", callId1);

        // 停止录像
        String callId2 = ServerSendCmd.deviceControlTeleBoot(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "StopRecord"
        );
        Assertions.assertNotNull(callId2);
        log.info("停止录像控制发送成功，callId: {}", callId2);
    }

    /**
     * 测试服务器端实时流邀请
     */
    @Test
    @Order(15)
    public void testInvitePlay() {
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
    @Order(16)
    public void testInvitePlayback() {
        log.info("测试服务器端录像回放邀请");

        Date startTime = new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000); // 2小时前
        Date endTime = new Date(System.currentTimeMillis() - 60 * 60 * 1000);       // 1小时前

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
     * 测试服务器端通道目录订阅
     */
    @Test
    @Order(17)
    public void testCatalogSubscribe() {
        log.info("测试服务器端通道目录订阅");

        String callId = ServerSendCmd.deviceCatalogSubscribe(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            3600, // 1小时
            "Catalog"
        );

        Assertions.assertNotNull(callId);
        log.info("通道目录订阅发送成功，callId: {}", callId);
    }

    /**
     * 测试服务器端移动位置订阅
     */
    @Test
    @Order(18)
    public void testMobilePositionSubscribe() {
        log.info("测试服务器端移动位置订阅");

        String callId = ServerSendCmd.devicePresetSubscribe(
            (FromDevice) serverFromDevice,
            (ToDevice) serverToDevice,
            "60", // 60秒间隔
            1800, // 30分钟
            "MobilePosition",
            "mobile-pos-001"
        );

        Assertions.assertNotNull(callId);
        log.info("移动位置订阅发送成功，callId: {}", callId);
    }

    @AfterEach
    public void tearDown() {
        log.info("清理服务器端测试环境");
    }
}