package io.github.lunasaw.gbproxy.test;

import javax.sip.message.Request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.gbproxy.test.user.client.DefaultRegisterProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/12
 */
@Slf4j
@SpringBootTest(classes = Gb28181ApplicationTest.class)
public class Gb28181TestClient {


    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Autowired
    @Qualifier("clientTo")
    private Device toDevice;

    @Autowired
    private SipLayer sipLayer;

    @AfterAll
    public static void after() {
        while (true) {

        }
    }

    @BeforeEach
    public void before() {
        // 本地端口监听
        log.info("before::客户端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        sipLayer.addListeningPoint("192.168.2.101", fromDevice.getPort(), true);

        DeviceConfig.DEVICE_CLIENT_VIEW_MAP.put(toDevice.getUserId(), toDevice);

    }

    @Test
    public void test_register_client() throws Exception {
        String callId = SipRequestUtils.getNewCallId();
        Request registerRequest = SipRequestProvider.createRegisterRequest((FromDevice) fromDevice, (ToDevice) toDevice, 300, callId);

        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }

    @Test
    public void a_test_register_client_custom() throws Exception {
        String callId = SipRequestUtils.getNewCallId();
        DefaultRegisterProcessorClient.isRegister = true;
        Request registerRequest = SipRequestProvider.createRegisterRequest((FromDevice) fromDevice, (ToDevice) toDevice, 300, callId);

        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }

    @Test
    public void b_test_un_register_client_custom() {
        Device instance = DeviceConfig.DEVICE_CLIENT_VIEW_MAP.get("41010500002000000001");
        DefaultRegisterProcessorClient.isRegister = false;
        ClientSendCmd.deviceUnRegister((FromDevice) fromDevice, (ToDevice) instance);
    }

    @Test
    public void test_keep_live() {
        ClientSendCmd.deviceKeepLiveNotify((FromDevice) fromDevice, (ToDevice) toDevice, "ok");
    }
}
