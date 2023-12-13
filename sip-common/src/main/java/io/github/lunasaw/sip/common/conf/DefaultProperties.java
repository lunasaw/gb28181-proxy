package io.github.lunasaw.sip.common.conf;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 获取sip默认配置
 *
 * @author lin
 */
@Slf4j
public class DefaultProperties {

    public static Properties getProperties(String name, String ip, boolean sipLog) {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", name);
        properties.setProperty("javax.sip.IP_ADDRESS", ip);


        /**
         * sip_server_log.log 和 sip_debug_log.log ERROR, INFO, WARNING, OFF, DEBUG, TRACE
         */
        try {
            File configFile = ResourceUtils.getFile("classpath:config.properties").getAbsoluteFile();
            properties.load(Files.newInputStream(configFile.toPath()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (sipLog) {
            properties.setProperty("gov.nist.javax.sip.STACK_LOGGER", "io.github.lunasaw.sip.common.conf.StackLoggerImpl");
            properties.setProperty("gov.nist.javax.sip.SERVER_LOGGER", "io.github.lunasaw.sip.common.conf.ServerLoggerImpl");
            properties.setProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT", "true");
            log.info("[SIP日志]已开启");
        } else {
            log.info("[SIP日志]已关闭");
        }
        return properties;
    }
}
