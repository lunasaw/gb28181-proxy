package io.github.lunasaw.sip.common.entity;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.Data;

/**
 * sip事物交换信息
 */
@Data
public class SipTransaction {

    private String callId;
    private String fromTag;
    private String toTag;
    private String viaBranch;

    public SipTransaction(SIPResponse response) {
        this.callId = response.getCallIdHeader().getCallId();
        this.fromTag = response.getFromTag();
        this.toTag = response.getToTag();
        this.viaBranch = response.getTopmostViaHeader().getBranch();
    }

    public SipTransaction(SIPRequest request) {
        this.callId = request.getCallIdHeader().getCallId();
        this.fromTag = request.getFromTag();
        this.toTag = request.getToTag();
        this.viaBranch = request.getTopmostViaHeader().getBranch();
    }
}
