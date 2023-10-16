package io.github.lunasaw.gbproxy.server.entity.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceQuery extends DeviceBase {


    public DeviceQuery() {
    }

    public DeviceQuery(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }


}
