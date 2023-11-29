package io.github.lunasaw.gbproxy.server.entity;

import io.github.lunasaw.sip.common.enums.ManufacturerEnum;
import io.github.lunasaw.sip.common.enums.StreamModeEnum;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Data;

/**
 * @author weidian
 * @date 2023/11/16
 */
@Data
public class InviteRequest {

    private Boolean        seniorSdp;
    private StreamModeEnum streamModeEnum;
    private String         userId;
    /**
     * 收流IP
     */
    private String         sdpIp;
    /**
     * 收流端口
     */
    private Integer        mediaPort;
    private String         ssrc;
    private Boolean        subStream;
    private ManufacturerEnum manufacturer;

    public InviteRequest(String userId, String sdpIp, Integer mediaPort) {
        this.seniorSdp = false;
        this.streamModeEnum = StreamModeEnum.TCP_PASSIVE;
        this.userId = userId;
        this.sdpIp = sdpIp;
        this.mediaPort = mediaPort;
        this.ssrc = SipUtils.genSsrc(userId);
    }

    public InviteRequest(Boolean seniorSdp, StreamModeEnum streamModeEnum, String userId, String sdpIp, Integer mediaPort, String ssrc) {
        this.seniorSdp = seniorSdp;
        this.streamModeEnum = streamModeEnum;
        this.userId = userId;
        this.sdpIp = sdpIp;
        this.mediaPort = mediaPort;
        this.ssrc = ssrc;
    }

    public String getContent() {
        return InviteEntity.getInvitePlayBody(streamModeEnum, userId, sdpIp, mediaPort, ssrc).toString();
    }

    public String getContentWithSub() {
        return InviteEntity.getInvitePlayBody(streamModeEnum, userId, sdpIp, mediaPort, ssrc, subStream, manufacturer).toString();
    }

    public String getContentWithSdp(Boolean seniorSdp) {
        return InviteEntity.getInvitePlayBody(seniorSdp, streamModeEnum, userId, sdpIp, mediaPort, ssrc).toString();
    }

    public String getSubject(String currentUserId) {
        return InviteEntity.getSubject(userId, ssrc, currentUserId);
    }
}
