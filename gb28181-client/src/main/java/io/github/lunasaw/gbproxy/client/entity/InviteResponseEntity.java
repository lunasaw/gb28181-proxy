package io.github.lunasaw.gbproxy.client.entity;

import io.github.lunasaw.gb28181.common.entity.enums.InviteSessionNameEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Random;

/**
 * @author luna
 * @date 2023/11/6
 */
@Getter
@Setter
public class InviteResponseEntity {

    /**
     * 点播类型
     */
    private InviteSessionNameEnum sessionName;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 媒体IP
     */
    private String mediaIp;
    /**
     * 本地端口
     */
    private int localPort;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * ssrc
     */
    private String ssrc;

    public static void main(String[] args) {

        StringBuffer content = getAckPlayBody("userId", "mediaIp", 0, "ssrc");

        System.out.println(content);
    }

    public static StringBuffer getAckPlayBackBody(String userId, String mediaIp, int localPort, long startTime, long endTime, String ssrc) {
        return getAckBody(InviteSessionNameEnum.PLAY_BACK, userId, mediaIp, localPort, startTime, endTime, ssrc);
    }

    public static StringBuffer getAckPlayBody(String userId, String mediaIp, int localPort, String ssrc) {
        return getAckBody(InviteSessionNameEnum.PLAY, userId, mediaIp, localPort, 0, 0, ssrc);
    }

    public static StringBuffer getAckBody(InviteSessionNameEnum sessionName, String userId, String mediaIp, int localPort, long startTime, long endTime, String ssrc) {
        StringBuffer content = new StringBuffer(200);
        content.append("v=0\r\n");
        content.append("o=").append(userId).append(" 0 0 IN IP4 ").append(mediaIp).append("\r\n");
        content.append("s=").append(sessionName.getType()).append("\r\n");
        content.append("c=IN IP4 ").append(mediaIp).append("\r\n");
        if (InviteSessionNameEnum.PLAY_BACK.equals(sessionName)) {
            content.append("t=").append(startTime).append(" ").append(endTime).append("\r\n");
        } else {
            content.append("t=0 0\r\n");
        }
        if (localPort == 0) {
            // 非严格模式端口不统一, 增加兼容性，修改为一个不为0的端口
            localPort = new Random().nextInt(65535) + 1;
        }
        content.append("m=video ").append(localPort).append(" RTP/AVP 96\r\n");
        content.append("a=sendonly\r\n");
        content.append("a=rtpmap:96 PS/90000\r\n");
        content.append("y=").append(ssrc).append("\r\n");
        content.append("f=\r\n");
        return content;
    }

    @Override
    public String toString() {
        return getAckBody(sessionName, userId, mediaIp, localPort, startTime, endTime, ssrc).toString();
    }
}
