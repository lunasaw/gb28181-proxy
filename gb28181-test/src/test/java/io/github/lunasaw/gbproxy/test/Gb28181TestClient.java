package io.github.lunasaw.gbproxy.test;

import javax.sip.message.Request;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;

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

    @BeforeEach
    public void before() {
        // 本地端口监听
        log.info("before::客户端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        SipLayer.addListeningPoint(DeviceConfig.LOOP_IP, fromDevice.getPort());
    }

    @Test
    public void test_register_client() throws Exception {
        String callId = SipRequestUtils.getNewCallId();
        Request registerRequest = SipRequestProvider.createRegisterRequest((FromDevice)fromDevice, (ToDevice)toDevice, callId, 300);

        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }


    @Test
    public void test_keep_live() {
        ClientSendCmd.deviceKeepLiveNotify((FromDevice)fromDevice, (ToDevice)toDevice, "ok");
    }

    @AfterEach
    public void after() {
        while (true) {

        }
    }
}
