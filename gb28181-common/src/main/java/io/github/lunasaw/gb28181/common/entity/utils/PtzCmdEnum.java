package io.github.lunasaw.gb28181.common.entity.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举类，表示不同的云台控制命令
 */
public enum PtzCmdEnum {
    // 向左移动
    LEFT("left", 2),
    // 向右移动
    RIGHT("right", 1),
    // 向上移动
    UP("up", 8),
    // 向下移动
    DOWN("down", 4),
    // 向左上移动
    UPLEFT("upleft", 10),
    // 向右上移动
    UPRIGHT("upright", 9),
    // 向左下移动
    DOWNLEFT("downleft", 6),
    // 向右下移动
    DOWNRIGHT("downright", 5),
    // 放大
    ZOOMIN("zoomin", 16),
    // 缩小
    ZOOMOUT("zoomout", 32);

    // 命令的字符串表示
    private String command;
    // 命令的数值编码
    private int cmdCode;

    // 构造方法，私有化
    private PtzCmdEnum(String command, int cmdCode) {
        this.command = command;
        this.cmdCode = cmdCode;
    }

    // 获取命令的字符串表示
    public String getCommand() {
        return command;
    }

    // 获取命令的数值编码
    public int getCmdCode() {
        return cmdCode;
    }

    // 用于存储命令和枚举值的映射关系
    private static Map<String, PtzCmdEnum> map = new HashMap<>();

    // 静态代码块，初始化映射关系
    static {
        for (PtzCmdEnum e : PtzCmdEnum.values()) {
            map.put(e.getCommand(), e);
        }
    }

    // 根据命令的字符串表示，获取对应的枚举值
    public static PtzCmdEnum getByCommand(String command) {
        return map.get(command);
    }
}