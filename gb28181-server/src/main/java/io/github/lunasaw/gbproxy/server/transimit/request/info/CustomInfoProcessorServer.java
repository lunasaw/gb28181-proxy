package io.github.lunasaw.gbproxy.server.transimit.request.info;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * @author luna
 * @date 2023/12/29
 */
public class CustomInfoProcessorServer implements InfoProcessorServer, InitializingBean {
    @Override
    public void dealInfo(String userId, String content) {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(this);
    }
}
