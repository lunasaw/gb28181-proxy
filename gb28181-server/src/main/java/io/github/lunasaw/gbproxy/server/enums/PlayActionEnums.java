package io.github.lunasaw.gbproxy.server.enums;

import io.github.lunasaw.gbproxy.server.entity.InviteEntity;
import lombok.Getter;
import org.springframework.util.Assert;

/**
 * 国标点播操作类型
 * 
 * @author luna
 */
@Getter
public enum PlayActionEnums {
    PLAY_RESUME("playResume", "回放暂停", null),
    PLAY_RANGE("playRange", "回放Seek", ""),
    PLAY_SPEED("playSpeed", "倍速回放", 1.0),
    PLAY_NOW("playNow", "继续回放", null);

    private final String type;
    private final String desc;

    private final Object data;


    PlayActionEnums(String type, String desc, Object data) {
        this.type = type;
        this.desc = desc;
        this.data = data;
    }

    public String getControlBody() {
        return getControlBody(data);
    }

    public String getControlBody(Object data) {
        if (PLAY_RESUME.equals(this)) {
            return InviteEntity.playPause();
        } else if (PLAY_RANGE.equals(this)) {
            return InviteEntity.playRange((Long) data);
        } else if (PLAY_SPEED.equals(this)) {
            return InviteEntity.playSpeed((Double) data);
        } else if (PLAY_NOW.equals(this)) {
            return InviteEntity.playNow();
        }
        return null;
    }
}