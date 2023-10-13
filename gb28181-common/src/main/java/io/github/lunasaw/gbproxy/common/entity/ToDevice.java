package io.github.lunasaw.gbproxy.common.entity;

import io.github.lunasaw.gbproxy.common.constant.Constant;
import io.github.lunasaw.gbproxy.common.enums.StreamModeEnum;
import io.github.lunasaw.gbproxy.common.enums.TransModeEnum;
import io.github.lunasaw.gbproxy.common.utils.SipRequestUtils;
import lombok.Data;

/**
 * @author luna
 * @date 2023/10/12
 */
@Data
public class ToDevice extends Device{

    /**
     * 及联的处理的时候，需要将上游的toTag往下带
     * toTag也是SIP协议中的一个字段，用于标识SIP消息的接收方。每个SIP消息都应该包含一个toTag字段，这个字段的值是由接收方生成的随机字符串，
     * 用于标识该消息的接收方。在SIP消息的传输过程中，每个中间节点都会将toTag字段的值保留不变，以确保消息的接收方不变。
     */
    private String toTag;

    /**
     * 需要想下游携带的信息
     */
    private String subject;

    public static ToDevice getInstance(String userId, String ip, int port) {
        ToDevice toDevice = new ToDevice();
        toDevice.setUserId(userId);
        toDevice.setIp(ip);
        toDevice.setPort(port);
        toDevice.setTransport(TransModeEnum.UDP.getType());
        toDevice.setStreamMode(StreamModeEnum.UDP.getType());
        toDevice.setToTag(null);
        return toDevice;
    }
}
