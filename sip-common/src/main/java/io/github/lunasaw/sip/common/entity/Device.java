package io.github.lunasaw.sip.common.entity;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author luna
 * @date 2023/10/12
 */
@Data
public abstract class Device {

    /**
     * 用户Id
     */
    private String userId;

    /**
     * 域
     */
    private String realm;

    /**
     * 传输协议
     * UDP/TCP
     */
    private String transport;

    /**
     * 数据流传输模式
     * UDP:udp传输
     * TCP-ACTIVE：tcp主动模式
     * TCP-PASSIVE：tcp被动模式
     */
    private String streamMode;

    /**
     * wan地址_ip
     */
    private String ip;

    /**
     * wan地址_port
     */
    private int    port;

    /**
     * wan地址
     */
    private String hostAddress;

    /**
     * 密码
     */
    private String password;


    /**
     * 编码
     */
    private String charset;

    public String getCharset() {
        if (this.charset == null) {
            return "GB2312";
        }
        return charset;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public String getHostAddress() {
        if (StringUtils.isBlank(hostAddress)) {
            return ip + ":" + port;
        }
        return hostAddress;
    }
}
