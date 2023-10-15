package io.github.lunasaw.gbproxy.server.cmd;

import com.luna.common.os.SystemInfoUtil;
import io.github.lunasaw.gbproxy.server.Gb28181Server;
import io.github.lunasaw.gbproxy.server.transimit.cmd.DeviceSendCmd;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.layer.SipLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author weidian
 * @date 2023/10/14
 */
@SpringBootTest(classes = Gb28181Server.class)
public class Applicationtest {


    FromDevice fromDevice;

    ToDevice   toDevice;

    static String localIp = SystemInfoUtil.getNoLoopbackIP();

    @BeforeEach
    public void before() {
        SipLayer.addListeningPoint(localIp, 8118);
        fromDevice = FromDevice.getInstance("41010500002000000010", localIp, 8118);
        toDevice = ToDevice.getInstance("33010602011187000001", localIp, 8117);
        toDevice.setPassword("weidian");
        toDevice.setRealm("4101050000");
    }

    @Test
    public void test_device_info() {
        String infoQueryCallId = DeviceSendCmd.deviceInfo(fromDevice, toDevice);
        System.out.println(infoQueryCallId);
    }
}
