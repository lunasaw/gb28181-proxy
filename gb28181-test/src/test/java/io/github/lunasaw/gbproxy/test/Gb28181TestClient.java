package io.github.lunasaw.gbproxy.test;

import javax.sip.message.Request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import com.alibaba.fastjson2.JSON;
import com.luna.common.text.RandomStrUtil;

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
@SpringBootTest(classes = Gb28181ApplicationTest.class)
public class Gb28181TestClient {


    @Autowired
    private Device fromDevice;

    @Autowired
    @Qualifier("toLocalDevice")
    private Device toLocalDevice;

    @BeforeEach
    public void before() {
        // 本地端口监听
        SipLayer.addListeningPoint(fromDevice.getIp(), fromDevice.getPort());


        toLocalDevice.setPassword("luna");
        toLocalDevice.setRealm("4101050000");
    }

    @Test
    public void btest() throws Exception {
        String callId = RandomStrUtil.getUUID();
        Request registerRequest = SipRequestProvider.createRegisterRequest((FromDevice) fromDevice, (ToDevice) toLocalDevice, callId, 300);

        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }


    @Test
    public void test_register_server() {

    }

    @AfterEach
    public void after() {
        while (true) {

        }
    }
}
