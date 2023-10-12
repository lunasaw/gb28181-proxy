package io.github.lunasaw.gbproxy.common.transmit.event;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author lin
 */
@Component
public class SipSubscribe {

    private final Logger         logger              = LoggerFactory.getLogger(SipSubscribe.class);

    private Map<String, Event>   errorSubscribes     = new ConcurrentHashMap<>();

    private Map<String, Event>   okSubscribes        = new ConcurrentHashMap<>();

    private Map<String, Instant> okTimeSubscribes    = new ConcurrentHashMap<>();

    private Map<String, Instant> errorTimeSubscribes = new ConcurrentHashMap<>();

    // @Scheduled(cron="*/5 * * * * ?") //每五秒执行一次
    // @Scheduled(fixedRate= 100 * 60 * 60 )
    @Scheduled(cron = "0 0/5 * * * ?") // 每5分钟执行一次
    public void execute() {
        logger.info("[定时任务] 清理过期的SIP订阅信息");

        Instant instant = Instant.now().minusMillis(TimeUnit.MINUTES.toMillis(5));

        for (String key : okTimeSubscribes.keySet()) {
            if (okTimeSubscribes.get(key).isBefore(instant)) {
                okSubscribes.remove(key);
                okTimeSubscribes.remove(key);
            }
        }
        for (String key : errorTimeSubscribes.keySet()) {
            if (errorTimeSubscribes.get(key).isBefore(instant)) {
                errorSubscribes.remove(key);
                errorTimeSubscribes.remove(key);
            }
        }
        logger.debug("okTimeSubscribes.size:{}", okTimeSubscribes.size());
        logger.debug("okSubscribes.size:{}", okSubscribes.size());
        logger.debug("errorTimeSubscribes.size:{}", errorTimeSubscribes.size());
        logger.debug("errorSubscribes.size:{}", errorSubscribes.size());
    }

    public void addErrorSubscribe(String key, Event event) {
        errorSubscribes.put(key, event);
        errorTimeSubscribes.put(key, Instant.now());
    }

    public void addOkSubscribe(String key, Event event) {
        okSubscribes.put(key, event);
        okTimeSubscribes.put(key, Instant.now());
    }

    public Event getErrorSubscribe(String key) {
        return errorSubscribes.get(key);
    }

    public void removeErrorSubscribe(String key) {
        if (key == null) {
            return;
        }
        errorSubscribes.remove(key);
        errorTimeSubscribes.remove(key);
    }

    public Event getOkSubscribe(String key) {
        return okSubscribes.get(key);
    }

    public void removeOkSubscribe(String key) {
        if (key == null) {
            return;
        }
        okSubscribes.remove(key);
        okTimeSubscribes.remove(key);
    }

    public int getErrorSubscribesSize() {
        return errorSubscribes.size();
    }

    public int getOkSubscribesSize() {
        return okSubscribes.size();
    }


}
