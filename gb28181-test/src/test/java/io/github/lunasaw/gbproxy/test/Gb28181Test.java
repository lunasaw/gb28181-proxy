package io.github.lunasaw.gbproxy.test;

import com.luna.common.text.RandomStrUtil;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.request.SipRequestProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sip.message.Request;

/**
 * @author luna
 * @date 2023/10/12
 */
@SpringBootTest(classes = Gb28181ApplicationTest.class)
public class Gb28181Test {

    private ToDevice toDevice;

    String localIp = "172.19.128.100";

    String remoteIp = "10.37.5.132";

    @BeforeEach
    public void before() {
        SipLayer.addListeningPoint(localIp, 8118);
        fromDevice = FromDevice.getInstance("33010602011187000001", localIp, 8118);
        toDevice = ToDevice.getInstance("41010500002000000010", remoteIp, 8116);
        toDevice.setPassword("luna");
        toDevice.setRealm("4101050000");
    }


    @Autowired
    private FromDevice fromDevice;

    @Test
    public void btest() throws Exception {
        String callId = RandomStrUtil.getUUID();
        Request registerRequest = SipRequestProvider.createRegisterRequest(fromDevice, toDevice, callId, 300);


        SipSender.transmitRequestSuccess(fromDevice.getIp(), registerRequest, new Event() {
            @Override
            public void response(EventResult eventResult) {
                System.out.println(eventResult);
            }
        });
    }

    @AfterEach
    public void after() {
        while (true) {

        }
    }
}
