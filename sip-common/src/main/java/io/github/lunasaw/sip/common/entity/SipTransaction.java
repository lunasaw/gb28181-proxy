package io.github.lunasaw.sip.common.entity;

import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.Data;

/**
 * sip事物交换信息
 * @author weidian
 */
@Data
public class SipTransaction {

    private String callId;
    private String fromTag;
    private String toTag;
    private String viaBranch;


}
