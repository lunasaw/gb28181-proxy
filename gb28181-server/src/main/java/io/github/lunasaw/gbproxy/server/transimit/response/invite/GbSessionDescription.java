package io.github.lunasaw.gbproxy.server.transimit.response.invite;

import javax.sdp.SessionDescription;

import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import lombok.Data;

/**
 * 28181 的SDP解析器
 *
 * @author luna
 */
@Data
public class GbSessionDescription extends SdpSessionDescription {

    private String ssrc;

    private String mediaDescription;

    public GbSessionDescription(SessionDescription sessionDescription) {
        super(sessionDescription);
    }

    public static GbSessionDescription getInstance(SessionDescription sessionDescription, String ssrc, String mediaDescription) {
        GbSessionDescription gbSessionDescription = new GbSessionDescription(sessionDescription);
        gbSessionDescription.setSsrc(ssrc);
        gbSessionDescription.setMediaDescription(mediaDescription);
        return gbSessionDescription;
    }

}
