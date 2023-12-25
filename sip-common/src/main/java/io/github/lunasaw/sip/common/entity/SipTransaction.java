package io.github.lunasaw.sip.common.entity;

import lombok.Data;

/**
 * sip事物交换信息
 * @author luna
 */
@Data
public class SipTransaction {

    private String callId;
    private String fromTag;
    private String toTag;
    private String viaBranch;


}
