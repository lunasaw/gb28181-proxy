package io.github.lunasaw.sip.common.transmit.event.handler;

import javax.sip.RequestEvent;

/**
 * @author weidian
 * @version 1.0
 * @date 2023/12/12
 * @description:
 */
public interface RequestHandler {
    /**
     * 响应ack
     *
     * @param event 请求事件
     */
    void responseAck(RequestEvent event);

    /**
     * 响应error
     *
     * @param event 请求事件
     */
    void responseError(RequestEvent event);

    /**
     * 处理消息
     *
     * @param event
     */
    void handForEvt(RequestEvent event);

    /**
     * 处理标签
     *
     * @return
     */
    String getRootType();

    /**
     * 处理消息类型
     *
     * @return
     */
    String getCmdType();

    /**
     * 当前接受到的原始消息
     */
    void setXmlStr(String xmlStr);
}