package io.github.lunasaw.gbproxy.test.user.client;

import io.github.lunasaw.gbproxy.client.transmit.request.bye.ByeProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/11/14
 */
@Component
public class DefaultByeProcessorClient implements ByeProcessorClient {

    @Autowired
    private FfmpegCommander ffmpegCommander;

    @Override
    public void closeStream(String callId) {
        ffmpegCommander.closeStream(callId);
    }

}
