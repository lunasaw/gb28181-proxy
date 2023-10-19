package io.github.lunasaw.sip.common.entity.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceMobileQuery extends XmlBean {
    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public String sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

    @XmlElement(name = "interval")
    private String Interval;

    public DeviceMobileQuery() {
    }

    public DeviceMobileQuery(String cmdType, String sn, String deviceId) {
        this.cmdType = cmdType;
        this.sn = sn;
        this.deviceId = deviceId;
    }


}
