package io.github.lunasaw.gbproxy.server.entity;

import io.github.lunasaw.sip.common.enums.ManufacturerEnum;
import io.github.lunasaw.sip.common.enums.StreamModeEnum;

/**
 * @author luna
 * @date 2023/11/6
 */
public class InviteEntity {

    public static void main(String[] args) {
        StringBuffer inviteBody = getInvitePlayBody(StreamModeEnum.UDP, "41010500002000000001", "127.0.0.1", 5060, "1234567890");
        System.out.println(inviteBody);
    }

    /**
     * 组装subject
     *
     * @param userId 设备Id
     * @param subId  通道Id
     * @param ssrc   混淆码
     * @return
     */
    public static String getSubject(String userId, String subId, String ssrc) {
        return String.format("%s:%s,%s:%s", subId, ssrc, userId, 0);
    }

    public static StringBuffer getInvitePlayBody(StreamModeEnum streamModeEnum, String userId, String sdpIp, Integer mediaPort, String ssrc) {
        return getInvitePlayBody(false, streamModeEnum, userId, sdpIp, mediaPort, ssrc, false, null);
    }

    public static StringBuffer getInvitePlayBody(Boolean seniorSdp, StreamModeEnum streamModeEnum, String userId, String sdpIp, Integer mediaPort, String ssrc) {
        return getInvitePlayBody(seniorSdp, streamModeEnum, userId, sdpIp, mediaPort, ssrc, false, null);
    }

    public static StringBuffer getInvitePlayBody(Boolean seniorSdp, StreamModeEnum streamModeEnum, String userId, String sdpIp, Integer mediaPort, String ssrc, Boolean subStream, String manufacturer) {
        StringBuffer content = new StringBuffer(200);
        content.append("v=0\r\n");
        content.append("o=").append(userId).append(" 0 0 IN IP4 ").append(sdpIp).append("\r\n");
        // Session Name
        content.append("s=Play\r\n");
        content.append("c=IN IP4 ").append(sdpIp).append("\r\n");
        content.append("t=0 0\r\n");

        if (seniorSdp) {
            if (StreamModeEnum.TCP_PASSIVE.equals(streamModeEnum)) {
                content.append("m=video ").append(mediaPort).append(" TCP/RTP/AVP 96 126 125 99 34 98 97\r\n");
            } else if (StreamModeEnum.TCP_ACTIVE.equals(streamModeEnum)) {
                content.append("m=video ").append(mediaPort).append(" TCP/RTP/AVP 96 126 125 99 34 98 97\r\n");
            } else if (StreamModeEnum.UDP.equals(streamModeEnum)) {
                content.append("m=video ").append(mediaPort).append(" RTP/AVP 96 126 125 99 34 98 97\r\n");
            }
            content.append("a=recvonly\r\n");
            content.append("a=rtpmap:96 PS/90000\r\n");
            content.append("a=fmtp:126 profile-level-id=42e01e\r\n");
            content.append("a=rtpmap:126 H264/90000\r\n");
            content.append("a=rtpmap:125 H264S/90000\r\n");
            content.append("a=fmtp:125 profile-level-id=42e01e\r\n");
            content.append("a=rtpmap:99 H265/90000\r\n");
            content.append("a=rtpmap:98 H264/90000\r\n");
            content.append("a=rtpmap:97 MPEG4/90000\r\n");
            if (StreamModeEnum.TCP_PASSIVE.equals(streamModeEnum)) {
                content.append("a=setup:passive\r\n");
                content.append("a=connection:new\r\n");
            } else if (StreamModeEnum.TCP_ACTIVE.equals(streamModeEnum)) {
                content.append("a=setup:active\r\n");
                content.append("a=connection:new\r\n");
            }
        } else {
            if (StreamModeEnum.TCP_PASSIVE.equals(streamModeEnum)) {
                content.append("m=video " + mediaPort + " TCP/RTP/AVP 96 97 98 99\r\n");
            } else if (StreamModeEnum.TCP_ACTIVE.equals(streamModeEnum)) {
                content.append("m=video " + mediaPort + " TCP/RTP/AVP 96 97 98 99\r\n");
            } else if (StreamModeEnum.UDP.equals(streamModeEnum)) {
                content.append("m=video " + mediaPort + " RTP/AVP 96 97 98 99\r\n");
            }
            content.append("a=recvonly\r\n");
            content.append("a=rtpmap:96 PS/90000\r\n");
            content.append("a=rtpmap:98 H264/90000\r\n");
            content.append("a=rtpmap:97 MPEG4/90000\r\n");
            content.append("a=rtpmap:99 H265/90000\r\n");
            if (StreamModeEnum.TCP_PASSIVE.equals(streamModeEnum)) {
                content.append("a=setup:passive\r\n");
                content.append("a=connection:new\r\n");
            } else if (StreamModeEnum.TCP_ACTIVE.equals(streamModeEnum)) {
                content.append("a=setup:active\r\n");
                content.append("a=connection:new\r\n");
            }
        }

        addSsrc(content, ssrc);

        addSubStream(content, subStream, manufacturer);

        return content;
    }

    public static StringBuffer addSsrc(StringBuffer content, String ssrc) {
        content.append("y=").append(ssrc).append("\r\n");//ssrc
        return content;
    }

    public static StringBuffer addSubStream(StringBuffer content, Boolean subStream, String manufacturer) {
        if (ManufacturerEnum.TP_LINK.getType().equals(manufacturer)) {
            if (subStream) {
                content.append("a=streamMode:sub\r\n");
            } else {
                content.append("a=streamMode:main\r\n");
            }
        } else {
            if (subStream) {
                content.append("a=streamprofile:1\r\n");
            } else {
                content.append("a=streamprofile:0\r\n");
            }
        }
        return content;
    }
}
