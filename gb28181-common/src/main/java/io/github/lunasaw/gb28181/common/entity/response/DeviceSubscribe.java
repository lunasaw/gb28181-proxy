package io.github.lunasaw.gb28181.common.entity.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.gb28181.common.entity.base.DeviceBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
 * <Response>
 * <CmdType>Catalog</CmdType>
 * <SN>sn</SN>
 * <DeviceID>channelId</DeviceID>
 * <Result>OK</Result>
 * </Response>
 * 
 * @author luna
 */
@Getter
@Setter
@NoArgsConstructor
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceSubscribe extends DeviceBase {

    /**
     * OK
     */
    @XmlElement(name = "Result")
    private String Result = "OK";

    public DeviceSubscribe(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }

}
