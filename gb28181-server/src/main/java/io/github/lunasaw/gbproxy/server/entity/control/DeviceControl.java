package io.github.lunasaw.gbproxy.server.entity.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.gbproxy.server.entity.DeviceBase;
import io.github.lunasaw.gbproxy.server.entity.query.DeviceQuery;
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
        this.cmdType = cmdType;
        this.sn = sn;
        this.deviceId = deviceId;
    }


}
