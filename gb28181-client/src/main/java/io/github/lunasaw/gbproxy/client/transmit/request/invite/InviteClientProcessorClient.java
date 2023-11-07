package io.github.lunasaw.gbproxy.client.transmit.request.invite;

import gov.nist.javax.sdp.TimeDescriptionImpl;
import gov.nist.javax.sdp.fields.TimeField;
import io.github.lunasaw.gbproxy.client.entity.InviteResponseEntity;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SessionDescription;
import java.time.Instant;
import java.util.Vector;

public interface InviteClientProcessorClient extends SipUserGenerate {


    void inviteSession(SdpSessionDescription sessionDescription);

    @SneakyThrows
    default String getAckContent(String userId, SdpSessionDescription sessionDescription) {

        SessionDescription baseSdb = sessionDescription.getBaseSdb();

        String address = baseSdb.getOrigin().getAddress();

        Instant start = null;
        Instant end = null;

        Vector timeDescriptions = baseSdb.getTimeDescriptions(false);
        if (CollectionUtils.isNotEmpty(timeDescriptions)) {

            TimeDescriptionImpl timeDescription = (TimeDescriptionImpl) timeDescriptions.get(0);
            TimeField startTimeFiled = (TimeField) timeDescription.getTime();

            start = Instant.ofEpochSecond(startTimeFiled.getStartTime());
            end = Instant.ofEpochSecond(startTimeFiled.getStopTime());

        }


        Vector mediaDescriptions = baseSdb.getMediaDescriptions(true);

        int port = -1;
        if (CollectionUtils.isNotEmpty(mediaDescriptions)) {
            for (int i = 0; i < mediaDescriptions.size(); i++) {
                MediaDescription mediaDescription = (MediaDescription) mediaDescriptions.get(i);
                Media media = mediaDescription.getMedia();
                Vector mediaFormats = media.getMediaFormats(false);
                if (mediaFormats.contains("98")) {
                    port = media.getMediaPort();
                    break;
                }
            }
        }

//        InviteResponseEntity.getAckPlayBackBody(baseSdb.getSessionName(), userId, address,
//                port, sessionDescription.getStartTime(), sessionDescription.getEndTime(), sessionDescription.getSsrc());

        return null;
    }

    ;
}