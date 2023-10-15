package io.github.lunasaw.sip.common.enums;

import java.util.HashMap;
import java.util.Map;

public enum DeviceGbType {
    CENTER_SERVER(200, "中心服务器"),
    DVR(111, "DVR"),
    NVR(118, "NVR"),
    CAMERA(132, "摄像机"),
    VIRTUAL_ORGANIZATION_DIRECTORY(215, "虚拟组织目录"),
    CENTER_SIGNAL_CONTROL_SERVER(216, "中心信令控制服务器");

    private static final Map<Integer, DeviceGbType> CODE_TO_TYPE_MAP = new HashMap<>();

    static {
        for (DeviceGbType type : values()) {
            CODE_TO_TYPE_MAP.put(type.code, type);
        }
    }

    private final int                               code;
    private final String                            description;

    DeviceGbType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DeviceGbType fromCode(int code) {
        return CODE_TO_TYPE_MAP.get(code);
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}