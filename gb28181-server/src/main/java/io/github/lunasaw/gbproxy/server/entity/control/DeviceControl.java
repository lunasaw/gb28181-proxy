package io.github.lunasaw.gbproxy.server.entity.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.DeviceBase;
import lombok.*;

/**
 * @author luna
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Control")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceControl extends DeviceBase {

    @XmlElement(name = "GuardCmd")
    public String guardCmd;

    @XmlElement(name = "IFameCmd")
    public String iFameCmd;

    public DeviceControl(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }


}
