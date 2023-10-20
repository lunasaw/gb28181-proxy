package io.github.lunasaw.sip.common.entity.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import lombok.Getter;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="gb2312"?>
 * <Query>
 *   <CmdType>Catalog</CmdType>
 *   <SN>123</SN>
 *   <DeviceID>123</DeviceID>
 * </Query>
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
