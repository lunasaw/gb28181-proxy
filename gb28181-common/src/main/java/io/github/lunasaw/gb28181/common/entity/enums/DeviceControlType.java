package io.github.lunasaw.gb28181.common.entity.enums;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.lunasaw.gb28181.common.entity.control.*;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import lombok.SneakyThrows;

@Getter
public enum DeviceControlType {

    /**
     * 云台控制
     * 上下左右，预置位，扫描，辅助功能，巡航
     */
    PTZ("PTZCmd", "云台控制", DeviceControlPtz.class),
    /**
     * 远程启动
     */
    TELE_BOOT("TeleBoot", "远程启动", DeviceControlTeleBoot.class),
    /**
     * 录像控制
     */
    RECORD("RecordCmd", "录像控制", DeviceControlRecordCmd.class),
    /**
     * 布防撤防
     */
    GUARD("GuardCmd", "布防撤防", DeviceControlGuard.class),
    /**
     * 告警控制
     */
    ALARM("AlarmCmd", "告警控制", DeviceControlAlarm.class),
    /**
     * 强制关键帧
     */
    I_FRAME("IFameCmd", "强制关键帧", DeviceControlIFame.class),
    /**
     * 拉框放大
     */
    DRAG_ZOOM_IN("DragZoomIn", "拉框放大", DeviceControlDragIn.class),
    /**
     * 拉框缩小
     */
    DRAG_ZOOM_OUT("DragZoomOut", "拉框缩小", DeviceControlDragOut.class),
    /**
     * 看守位
     */
    HOME_POSITION("HomePosition", "看守位", DeviceControlPosition.class);

    private static final Map<String, DeviceControlType> MAP = new ConcurrentHashMap<>();
    private final String val;
    private final String desc;
    private final Class<?> clazz;

    DeviceControlType(String val, String desc, Class<?> clazz) {
        this.val = val;
        this.desc = desc;
        this.clazz = clazz;
    }

    static {
        // MAP 初始化
        for (DeviceControlType deviceControlType : DeviceControlType.values()) {
            MAP.put(deviceControlType.getVal(), deviceControlType);
        }
    }

    public static DeviceControlType getDeviceControlTypeFilter(String content) {
        if (ObjectUtils.isEmpty(content)) {
            return null;
        }
        String key = MAP.keySet().stream().filter(content::contains).findFirst().orElse(StringUtils.EMPTY);

        return getDeviceControlType(key);
    }

    @SneakyThrows
    public static DeviceControlType getDeviceControlType(String key) {
        if (key == null) {
            return null;
        }
        if (MAP.containsKey(key)) {
            return MAP.get(key);
        }
        return null;
    }

}
