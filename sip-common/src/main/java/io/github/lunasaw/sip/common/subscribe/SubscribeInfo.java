package io.github.lunasaw.sip.common.subscribe;

import javax.sip.header.EventHeader;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luna
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeInfo {

    private String id;
    private int expires;
    private String eventId;
    private String eventType;
    /**
     * 上一次的请求
     */
    private SIPRequest request;
    private SIPResponse response;
    /**
     * 以下为可选字段
     */
    private String sn;
    private int gpsInterval;

    private String subscriptionState;

    public SubscribeInfo(SIPRequest request, String id) {
        this.id = id;
        this.request = request;
        this.expires = request.getExpires().getExpires();
        EventHeader eventHeader = (EventHeader) request.getHeader(EventHeader.NAME);
        this.eventId = eventHeader.getEventId();
        this.eventType = eventHeader.getEventType();
    }

}
