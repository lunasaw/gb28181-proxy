package io.github.lunasaw.gbproxy.test.user.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class FfmpegCommander {

    private static final String path = "/usr/local/bin/ffmpeg";
    private static final String               cmd        =
        "-re -stream_loop -1 -i {filePath} -vcodec h264 -acodec aac -f rtsp -rtsp_transport tcp rtsp://{ip}:{port}/rtsp/live?sign=41db35390ddad33f83944f44b8b75ded";

    private static final Map<String, Process> processMap = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(FfmpegCommander.class);

    public void pushStream(String callId, String filePath, String ip, int port) {
        String command = path + " " +
            cmd.replace("{filePath}", filePath).replace("{ip}", ip).replace("{port}", 1554 + "");
        logger.info("callId={},\r\n推流命令={}", callId, command);
        Runtime runtime = Runtime.getRuntime();
        try {
            new Thread(() -> {
                int code = 0;
                try {
                    Process process = runtime.exec(command);
                    processMap.put(callId, process);
                    InputStream errorInputStream = process.getErrorStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(errorInputStream));
                    StringBuffer errorStr = new StringBuffer();
                    String str;
                    while ((str = reader.readLine()) != null) {
                        errorStr.append(str);
                        logger.debug(str);
                    }
                    code = process.waitFor();
                    logger.info("推流已结束,callId={}", callId);
                } catch (Exception e) {
                    logger.error("ffmpeg推流异常!", e);
                }
            }).start();
        } catch (Exception e) {
            log.error("pushStream::callId = {}, filePath = {}, ip = {}, port = {} ", callId, filePath, ip, port, e);
        }
    }

    public void closeStream(String callId) {
        logger.info("关闭推流:{}", callId);
        if (StringUtils.isEmpty(callId)) {
            closeAllStream();
        } else if (processMap.containsKey(callId)) {
            processMap.get(callId).destroy();
        } else {
            logger.info("没有推流要关闭!");
        }
    }

    public void closeAllStream() {
        logger.info("关闭所有推流");
        processMap.forEach((key, value) -> value.destroy());
    }
}
