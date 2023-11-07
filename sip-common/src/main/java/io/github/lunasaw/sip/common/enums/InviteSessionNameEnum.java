package io.github.lunasaw.sip.common.enums;

/**
 * @author luna
 * @date 2023/10/13
 */
public enum InviteSessionNameEnum {

    /**
     *
     */
    PLAY("play", "点播"),
    PLAY_BACK("playBack", "回放"),


    ;

    private final String type;
    private final String desc;

    InviteSessionNameEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static boolean isValid(String sort) {
        return PLAY.getType().equals(sort) || PLAY_BACK.getType().equals(sort);
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
