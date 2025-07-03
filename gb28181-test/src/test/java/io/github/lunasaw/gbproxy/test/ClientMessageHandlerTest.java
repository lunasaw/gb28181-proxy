package io.github.lunasaw.gbproxy.test;

import io.github.lunasaw.gb28181.common.entity.query.*;
import io.github.lunasaw.gb28181.common.entity.response.*;
import io.github.lunasaw.gb28181.common.entity.control.*;
import io.github.lunasaw.gb28181.common.entity.notify.*;
import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 客户端消息处理器专门测试
 * 测试客户端对服务器端查询请求的响应处理
 * 
 * @author luna
 * @date 2023/10/30
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientMessageHandlerTest {

    @Autowired
    @Qualifier("clientFrom")
    private Device clientFromDevice;

    @Autowired
    @Qualifier("clientTo")
    private Device clientToDevice;

    @Autowired
    private SipLayer sipLayer;

    @BeforeEach
    public void setUp() {
        log.info("初始化客户端测试环境");
        sipLayer.addListeningPoint(DeviceConfig.LOOP_IP, clientFromDevice.getPort(), true);
        DeviceConfig.DEVICE_CLIENT_VIEW_MAP.put(clientToDevice.getUserId(), clientToDevice);
    }

    /**
     * 测试客户端处理设备信息查询
     */
    @Test
    @Order(1)
    public void testHandleDeviceInfoQuery() {
        log.info("测试客户端处理设备信息查询");

        // 模拟收到设备信息查询请求后的响应
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceName("GB28181测试设备");
        deviceInfo.setManufacturer("测试厂商");
        deviceInfo.setModel("GB28181-Client");
        deviceInfo.setFirmware("2.0.0");
        deviceInfo.setChannel(32);
        deviceInfo.setResult("OK");

        String callId = ClientSendCmd.deviceInfoResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceInfo
        );

        Assertions.assertNotNull(callId);
        log.info("设备信息查询响应发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端处理通道目录查询
     */
    @Test
    @Order(2)
    public void testHandleCatalogQuery() {
        log.info("测试客户端处理通道目录查询");

        List<DeviceItem> deviceItems = createMockDeviceItems(10);

        String callId = ClientSendCmd.deviceChannelCatalogResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceItems
        );

        Assertions.assertNotNull(callId);
        log.info("通道目录查询响应发送成功，callId: {}, 通道数量: {}", callId, deviceItems.size());
    }

    /**
     * 测试客户端处理分页通道目录查询
     */
    @Test
    @Order(3)
    public void testHandlePaginatedCatalogQuery() {
        log.info("测试客户端处理分页通道目录查询");

        // 创建大量通道模拟分页场景
        List<DeviceItem> deviceItems = createMockDeviceItems(50);

        // 使用分页响应方法
        ClientSendCmd.deviceChannelCatalogResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            deviceItems,
            "catalog-query-sn-001"
        );

        log.info("分页通道目录响应发送成功，总通道数: {}", deviceItems.size());
    }

    /**
     * 测试客户端处理录像查询
     */
    @Test
    @Order(4)
    public void testHandleRecordQuery() {
        log.info("测试客户端处理录像查询");

        List<DeviceRecord.RecordItem> recordItems = createMockRecordItems(15);

        ClientSendCmd.deviceRecordResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            recordItems,
            "record-query-sn-001"
        );

        log.info("录像查询响应发送成功，录像数量: {}", recordItems.size());
    }

    /**
     * 测试客户端处理设备配置查询
     */
    @Test
    @Order(5)
    public void testHandleConfigQuery() {
        log.info("测试客户端处理设备配置查询");

        DeviceConfigResponse.BasicParam basicParam = new DeviceConfigResponse.BasicParam();
        basicParam.setName("测试设备配置");
        basicParam.setExpiration("3600");
        basicParam.setHeartBeatInterval("60");
        basicParam.setHeartBeatCount("3");

        String callId = ClientSendCmd.deviceConfigResponse(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            basicParam
        );

        Assertions.assertNotNull(callId);
        log.info("设备配置查询响应发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端主动发送心跳
     */
    @Test
    @Order(6)
    public void testClientHeartbeat() {
        log.info("测试客户端主动发送心跳");

        String callId = ClientSendCmd.deviceKeepLiveNotify(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            "OK"
        );

        Assertions.assertNotNull(callId);
        log.info("心跳发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端发送移动位置信息
     */
    @Test
    @Order(7)
    public void testClientMobilePosition() {
        log.info("测试客户端发送移动位置信息");

        MobilePositionNotify mobilePositionNotify = new MobilePositionNotify();
        mobilePositionNotify.setTime("2023-11-01T10:00:00");
        mobilePositionNotify.setLongitude("116.397128");
        mobilePositionNotify.setLatitude("39.916527");
        mobilePositionNotify.setSpeed("30.5");
        mobilePositionNotify.setDirection("45.0");
        mobilePositionNotify.setAltitude("100.0");

        String callId = ClientSendCmd.MobilePositionNotify(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            mobilePositionNotify,
            null
        );

        Assertions.assertNotNull(callId);
        log.info("移动位置信息发送成功，callId: {}", callId);
    }

    /**
     * 测试客户端发送媒体状态通知
     */
    @Test
    @Order(8)
    public void testClientMediaStatus() {
        log.info("测试客户端发送媒体状态通知");

        // 121表示流媒体初始化完成
        String callId = ClientSendCmd.deviceMediaStatusNotify(
            (FromDevice) clientFromDevice,
            (ToDevice) clientToDevice,
            "121"
        );

        Assertions.assertNotNull(callId);
        log.info("媒体状态通知发送成功，callId: {}", callId);
    }

    /**
     * 创建模拟设备通道项
     */
    private List<DeviceItem> createMockDeviceItems(int count) {
        List<DeviceItem> deviceItems = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            DeviceItem item = new DeviceItem();
            item.setDeviceId(clientFromDevice.getUserId() + String.format("%03d", i));
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
            item.setLongitude(116.397128 + i * 0.001);
            item.setLatitude(39.916527 + i * 0.001);
            deviceItems.add(item);
        }
        return deviceItems;
    }

    /**
     * 创建模拟录像项
     */
    private List<DeviceRecord.RecordItem> createMockRecordItems(int count) {
        List<DeviceRecord.RecordItem> recordItems = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            DeviceRecord.RecordItem item = new DeviceRecord.RecordItem();
            item.setDeviceId(clientFromDevice.getUserId());
            item.setName("录像文件" + i);
            item.setFilePath("/record/path/file" + i + ".mp4");
            item.setStartTime("2023-11-" + String.format("%02d", (i % 30) + 1) + "T08:00:00");
            item.setEndTime("2023-11-" + String.format("%02d", (i % 30) + 1) + "T09:00:00");
            item.setSecrecy("0");
            item.setType("time");
            item.setFileSize(String.valueOf(1024 * 1024 * (i * 10))); // 模拟文件大小
            recordItems.add(item);
        }
        return recordItems;
    }

    @AfterEach
    public void tearDown() {
        log.info("清理客户端测试环境");
    }
}