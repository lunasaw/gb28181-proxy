package io.github.lunasaw.gbproxy.server.entity.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * <?xml version="1.0" encoding="gb2312"?>
 * <Control>
 * <CmdType>DeviceConfig</CmdType>
 * <SN>150959</SN>
 * <DeviceID>channelId</DeviceID>
 * <BasicParam>
 * <Name>name</Name>
 * <Expiration>30</Expiration>
 * <HeartBeatInterval>300</HeartBeatInterval>
 * <HeartBeatCount>300</HeartBeatCount>
 * </BasicParam>
 * </Control>
 * 
 * @author luna
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceConfigDownload extends DeviceQuery {


    @XmlElement(name = "ConfigType")
    public String configType;

    public DeviceConfigDownload(String cmdType, String sn, String deviceId) {
        this.cmdType = cmdType;
        this.sn = sn;
        this.deviceId = deviceId;
    }

}
