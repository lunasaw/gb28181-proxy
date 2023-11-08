package io.github.lunasaw.sip.common.transmit.event;

/**
 * 事件类型
 * @author luna
 */
public enum EventResultType {
    // ack
    ack,
    // 超时
    timeout,
    // 回复
    response,
    // 事务已结束
    transactionTerminated,
    // 会话已结束
    dialogTerminated,
    // 设备未找到
    deviceNotFoundEvent,
    // 设备未找到
    cmdSendFailEvent
}
