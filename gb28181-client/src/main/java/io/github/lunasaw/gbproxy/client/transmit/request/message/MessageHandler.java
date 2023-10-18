package io.github.lunasaw.gbproxy.client.transmit.request.message;

import javax.sip.RequestEvent;

public interface MessageHandler {
    /**
     * 处理消息
     *
     * @param evt
     */
    void handForEvt(RequestEvent evt);

    /**
     * 处理消息类型
     *
     * @return
     */
    String getCmdType();
}
