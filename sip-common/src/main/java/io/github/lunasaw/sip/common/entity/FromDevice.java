package io.github.lunasaw.sip.common.entity;

import io.github.lunasaw.sip.common.constant.Constant;
import io.github.lunasaw.sip.common.enums.StreamModeEnum;
import io.github.lunasaw.sip.common.enums.TransModeEnum;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import lombok.Data;

/**
 * @author luna
 * @date 2023/10/12
 */
@Data
public class FromDevice extends Device {

    /**
     * fromTag用于标识SIP消息的发送方，每个SIP消息都应该包含一个fromTag字段，这个字段的值是由发送方生成的随机字符串，用于标识该消息的发送方。在SIP消息的传输过程中，每个中间节点都会将fromTag字段的值保留不变，以确保消息的发送方不变。
     */
    private String fromTag;

    /**
     * 发送设备标识 类似浏览器User-Agent
     */
    private String agent;

    public static FromDevice getInstance(String userId, String ip, int port) {
        FromDevice fromDevice = new FromDevice();
        fromDevice.setUserId(userId);
        fromDevice.setIp(ip);
        fromDevice.setPort(port);
        fromDevice.setTransport(TransModeEnum.UDP.getType());
        fromDevice.setStreamMode(StreamModeEnum.UDP.getType());
        fromDevice.setFromTag(SipRequestUtils.getNewFromTag());
        fromDevice.setAgent(Constant.AGENT);
        return fromDevice;
    }

}
