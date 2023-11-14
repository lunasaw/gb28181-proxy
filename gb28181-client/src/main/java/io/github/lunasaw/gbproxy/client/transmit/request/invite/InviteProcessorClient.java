package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import gov.nist.javax.sdp.TimeDescriptionImpl;
import gov.nist.javax.sdp.fields.TimeField;
import io.github.lunasaw.gbproxy.client.entity.InviteResponseEntity;
import io.github.lunasaw.sip.common.entity.GbSessionDescription;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import io.github.lunasaw.sip.common.enums.InviteSessionNameEnum;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SessionDescription;
import java.util.Vector;

public interface InviteProcessorClient extends SipUserGenerate {


    void inviteSession(String callId, SdpSessionDescription sessionDescription);

    @SneakyThrows
    default String getAckContent(String userId, GbSessionDescription sessionDescription) {

        SessionDescription baseSdb = sessionDescription.getBaseSdb();
        String address = baseSdb.getOrigin().getAddress();
        String sessionName = baseSdb.getSessionName().getValue();
        sessionDescription.setAddress(address);

        Vector mediaDescriptions = baseSdb.getMediaDescriptions(true);

        int port = -1;
        if (CollectionUtils.isNotEmpty(mediaDescriptions)) {
            for (Object description : mediaDescriptions) {
                MediaDescription mediaDescription = (MediaDescription) description;
                Media media = mediaDescription.getMedia();
                Vector mediaFormats = media.getMediaFormats(false);
                if (mediaFormats.contains("98")) {
                    port = media.getMediaPort();
                    sessionDescription.setPort(port);
                    break;
                }
            }
        }


        if (InviteSessionNameEnum.PLAY_BACK.getType().equals(sessionName)) {

            Vector timeDescriptions = baseSdb.getTimeDescriptions(false);
            if (CollectionUtils.isEmpty(timeDescriptions)) {
                return InviteResponseEntity.getAckPlayBody(userId, address,
                        port, sessionDescription.getSsrc()).toString();

            }
            TimeDescriptionImpl timeDescription = (TimeDescriptionImpl) timeDescriptions.get(0);
            TimeField startTimeFiled = (TimeField) timeDescription.getTime();

            return InviteResponseEntity.getAckPlayBackBody(userId, address,
                    port, startTimeFiled.getStartTime(), startTimeFiled.getStopTime(), sessionDescription.getSsrc()).toString();
        }

        return InviteResponseEntity.getAckPlayBody(userId, address,
                port, sessionDescription.getSsrc()).toString();
    }
}