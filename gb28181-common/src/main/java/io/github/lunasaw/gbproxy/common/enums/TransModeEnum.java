package io.github.lunasaw.gbproxy.common.enums;

import io.github.lunasaw.gbproxy.common.constant.Constant;

/**
 * @author luna
 * @date 2023/10/13
 */
public enum TransModeEnum {

    /**
     * 传输模式
     */
    UDP(Constant.UDP, "UDP"),
    TCP(Constant.TCP, "TCP"),

    ;

    private final String type;
    private final String desc;

    TransModeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isValid(String sort) {
        return UDP.getType().equals(sort) || TCP.getType().equals(sort);
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
