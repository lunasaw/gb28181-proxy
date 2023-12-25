package io.github.lunasaw.gb28181.common.entity.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;

/**
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Control")
@XmlAccessorType(XmlAccessType.FIELD)
public class ControlBase extends DeviceControlBase {


    public ControlBase() {
    }

    public ControlBase(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }

}
