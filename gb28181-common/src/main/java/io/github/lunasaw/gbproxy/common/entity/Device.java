package io.github.lunasaw.gbproxy.common.entity;

import lombok.Data;

/**
 * @author luna
 * @date 2023/10/12
 */
@Data
public class Device {

    /**
     * 设备国标编号
     */
    private String deviceId;

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
}
