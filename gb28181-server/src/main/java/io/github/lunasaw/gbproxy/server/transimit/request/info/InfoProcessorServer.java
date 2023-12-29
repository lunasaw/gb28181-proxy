package io.github.lunasaw.gbproxy.server.transimit.request.info;

/**
 * @author luna
 * @date 2023/12/25
 */
public interface InfoProcessorServer {

    void dealInfo(String userId, String content);
}
