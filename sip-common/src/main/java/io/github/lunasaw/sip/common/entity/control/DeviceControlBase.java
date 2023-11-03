package io.github.lunasaw.sip.common.entity.control;

import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author luna
 */
@Getter
@Setter
public class DeviceControlBase extends DeviceBase {

    @XmlElement(name = "ControlType")
    private String controlType;

    public DeviceControlBase() {
    }

    public DeviceControlBase(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }

}
