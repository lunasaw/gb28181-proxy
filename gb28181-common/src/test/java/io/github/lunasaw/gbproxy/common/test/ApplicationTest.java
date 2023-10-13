package io.github.lunasaw.gbproxy.common.test;

import javax.sip.message.Request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.luna.common.os.SystemInfoUtil;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.common.Gb28181Common;
import io.github.lunasaw.gbproxy.common.entity.FromDevice;
import io.github.lunasaw.gbproxy.common.entity.ToDevice;
import io.github.lunasaw.gbproxy.common.layer.SipLayer;
import io.github.lunasaw.gbproxy.common.transmit.SipSender;
import io.github.lunasaw.gbproxy.common.transmit.cmd.SIPRequestHeaderProvider;
import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/13
 */
@SpringBootTest(classes = Gb28181Common.class)
public class ApplicationTest {

    @Autowired
    private SipSender sipSender;

    @Autowired
    private SipLayer sipLayer;

    @BeforeEach
    public void before() {
        sipLayer.addListeningPoint(SystemInfoUtil.getIP(), 8116);
    }

    @SneakyThrows
    @Test
    public void atest() {

        FromDevice fromDevice = FromDevice.getInstance("33010602011187000001", "172.19.128.100", 8116);

        ToDevice toDevice = ToDevice.getInstance("41010500002000000010", "10.37.5.132", 8116);

        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SIPRequestHeaderProvider.createMessageRequest(fromDevice, toDevice, "123123", callId);

        sipSender.transmitRequest(fromDevice.getIp(), messageRequest);
    }
}
