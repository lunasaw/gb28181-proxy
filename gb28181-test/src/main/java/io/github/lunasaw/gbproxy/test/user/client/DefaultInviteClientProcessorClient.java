package io.github.lunasaw.gbproxy.test.user.client;

import com.alibaba.fastjson2.JSON;
import io.github.lunasaw.gbproxy.client.transmit.request.invite.InviteClientProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.GbSessionDescription;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

/**
 * @author luna
 * @date 2023/11/7
 */
@Slf4j
@Component
public class DefaultInviteClientProcessorClient implements InviteClientProcessorClient {

    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;

    @Autowired
    private FfmpegCommander ffmpegCommander;

    private static String VIDEO_FILE;

    private static String RECORD_VIDEO_FILE;

    static {
        try {
            VIDEO_FILE = ResourceUtils.getFile("classpath:device/videofile.h264").getAbsolutePath();
            RECORD_VIDEO_FILE = ResourceUtils.getFile("classpath:device/record.h264").getAbsolutePath();
        } catch (FileNotFoundException e) {

        }
    }

    @Override
    public void inviteSession(String callId, SdpSessionDescription sessionDescription) {
        GbSessionDescription gbSessionDescription = (GbSessionDescription) sessionDescription;
        log.info("inviteSession::sessionDescription = {}", JSON.toJSONString(sessionDescription));

        SipSubscribe.addOkSubscribe(callId, eventResult -> {
            ffmpegCommander.closeAllStream();
            ffmpegCommander.pushStream(eventResult.callId, VIDEO_FILE, gbSessionDescription.getAddress(), gbSessionDescription.getPort());
        });
    }

    @Override
    public Device getToDevice(String userId) {
        return DefaultRegisterProcessorClient.deviceMap.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
