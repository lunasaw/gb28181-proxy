package io.github.lunasaw.gbproxy.test;

import javax.sip.message.Request;

import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
public class Gb28181Test {

    @Autowired
    private Device toDevice;
    @Autowired
    private Device fromDevice;

    @Autowired
    private Device serverDevice;

    @BeforeEach
    public void before() {
        // 本地端口监听
        SipLayer.addListeningPoint(serverDevice.getIp(), serverDevice.getPort());
        SipLayer.addListeningPoint(fromDevice.getIp(), fromDevice.getPort());
        // fromDevice = FromDevice.getInstance("33010602011187000001", localIp, 8118);
        // toDevice = ToDevice.getInstance("41010500002000000010", remoteIp, 8116);
        toDevice.setPassword("luna");
        toDevice.setRealm("4101050000");

        serverDevice.setPassword("luna");
        serverDevice.setRealm("4101050000");
    }

    @Test
    public void btest() throws Exception {
        String callId = SipRequestUtils.getNewCallId();
        Request registerRequest = SipRequestProvider.createRegisterRequest((FromDevice) fromDevice, (ToDevice) serverDevice, callId, 300);

        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }

    @Test
    public void test_cast_device() {
        System.out.println(JSON.toJSONString(fromDevice));
        System.out.println(JSON.toJSONString(toDevice));
        System.out.println(JSON.toJSONString(serverDevice));
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
