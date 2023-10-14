package io.github.lunasw.gbproxy.client.test.cmd;

import javax.sip.message.Request;

import io.github.lunasaw.gbproxy.client.transmit.cmd.SIPRequestHeaderProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.luna.common.os.SystemInfoUtil;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gbproxy.common.Gb28181Common;
import io.github.lunasaw.gbproxy.common.entity.FromDevice;
import io.github.lunasaw.gbproxy.common.entity.ToDevice;
import io.github.lunasaw.gbproxy.common.layer.SipLayer;
import io.github.lunasaw.gbproxy.common.transmit.SipSender;
import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/13
 */
@SpringBootTest(classes = Gb28181Common.class)
public class ApplicationTest {


    @BeforeEach
    public void before() {
        SipLayer.addListeningPoint(SystemInfoUtil.getIP(), 8117);
    }

    @SneakyThrows
    @Test
    public void atest() {

        FromDevice fromDevice = FromDevice.getInstance("33010602011187000001", SystemInfoUtil.getIP(), 8117);

        ToDevice toDevice = ToDevice.getInstance("41010500002000000010", "192.168.2.102", 8116);

        String callId = RandomStrUtil.getUUID();
        Request messageRequest = SIPRequestHeaderProvider.createMessageRequest(fromDevice, toDevice, "123123", callId);

        SipSender.transmitRequest(fromDevice.getIp(), messageRequest);
    }
}
