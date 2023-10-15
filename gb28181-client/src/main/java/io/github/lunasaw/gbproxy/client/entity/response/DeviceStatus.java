package io.github.lunasaw.gbproxy.client.entity.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.DeviceBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="gb2312"?>
 * <Response>
 * <CmdType>DeviceStatus</CmdType>
 * <SN>sn</SN>
 * <DeviceID>channelId</DeviceID>
 * <Result>OK</Result>
 * <Online>statusStr</Online>
 * <Status>OK</Status>
 * </Response>
 * 
 * @author luna
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceStatus extends DeviceBase {

    /**
     * OK
     */
    @XmlElement(name = "Result")
    private String    Result;

    /**
     * "ONLINE":"OFFLINE"
     */
    @XmlElement(name = "Online")
    private String Online;

    /**
     * OK
     */
    @XmlElement(name = "Status")
    private String status;

    public DeviceStatus(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }

}
