package io.github.lunasaw.sipproxy.common.test;

import io.github.lunasaw.sipproxy.common.Gb28181Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.luna.common.os.SystemInfoUtil;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.sipproxy.common.entity.FromDevice;
import io.github.lunasaw.sipproxy.common.entity.ToDevice;
import io.github.lunasaw.sipproxy.common.layer.SipLayer;
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

    }
}
