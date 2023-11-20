package io.github.lunasaw.gbproxy.test;

import javax.sip.message.Request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

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
public class Gb28181TestOtherClient {

    private static final String MONITOR_IP = "172.24.48.27";
    @Autowired
    @Qualifier("clientFrom")
    private Device              fromDevice;

    @Autowired
    @Qualifier("clientTo")
    private Device              toDevice;

    @AfterAll
    public static void after() {
        while (true) {

        }
    }

    @BeforeEach
    public void before() {
        // 本地端口监听
        log.info("before::客户端初始化 fromDevice.ip : {} , fromDevice.port : {}", fromDevice.getIp(), fromDevice.getPort());
        SipLayer.addListeningPoint(MONITOR_IP, fromDevice.getPort());

    }

    @Test
    public void test_register_client() {
        String callId = SipRequestUtils.getNewCallId();

        ToDevice instance = ToDevice.getInstance("41010500002000000001", "10.37.2.198", 8116);
        instance.setPassword("bajiuwulian1006");

        DefaultRegisterProcessorClient.deviceMap.put("41010500002000000001", instance);
        fromDevice.setIp(MONITOR_IP);

        Request registerRequest = SipRequestProvider.createRegisterRequest((FromDevice)fromDevice, instance, 300, callId);

        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }

}
