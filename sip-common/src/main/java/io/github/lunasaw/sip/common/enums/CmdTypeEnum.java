package io.github.lunasaw.sip.common.enums;

import io.github.lunasaw.sip.common.constant.Constant;

/**
 * @author luna
 * @date 2023/10/13
 */
public enum CmdTypeEnum {

    /**
     * 请求类型
     */
    Device_Info("DeviceInfo", "查询设备信息"),
    TCP(Constant.TCP, "TCP"),

    ;

    private final String type;
    private final String desc;

    CmdTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
