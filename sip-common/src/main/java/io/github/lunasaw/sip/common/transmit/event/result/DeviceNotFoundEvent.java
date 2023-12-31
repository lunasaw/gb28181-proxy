package io.github.lunasaw.sip.common.transmit.event.result;

import lombok.Getter;

import java.util.EventObject;

import javax.sip.Dialog;

/**
 * @author luna
 */
@Getter
public class DeviceNotFoundEvent extends EventObject {

    private String callId;

    /**
     * Constructs a prototypical Event.
     *
     * @param dialog
     * @throws IllegalArgumentException if source is null.
     */
    public DeviceNotFoundEvent(Dialog dialog) {
        super(dialog);
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
