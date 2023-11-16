package io.github.lunasaw.gbproxy.server.entity;

import io.github.lunasaw.sip.common.enums.StreamModeEnum;
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
    private String         sdpIp;
    private Integer        mediaPort;
    private String         ssrc;
    private Boolean        subStream;
    private String         manufacturer;

}
