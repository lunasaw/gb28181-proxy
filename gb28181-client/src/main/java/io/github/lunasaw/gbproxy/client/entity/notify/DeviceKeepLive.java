package io.github.lunasaw.gbproxy.client.entity.notify;

import javax.xml.bind.annotation.*;

import io.github.lunasaw.sip.common.entity.xml.DeviceBase;
import lombok.Getter;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="gb2312"?>
 * <Notify>
 * <CmdType>Keepalive</CmdType>
 * <SN>340917</SN>
 * <DeviceID>parentPlatform.getDeviceGBId()</DeviceID>
 * <Status>OK</Status>
 * </Notify>
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Notify")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceKeepLive extends DeviceBase {


    @XmlElement(name = "Status")
    public String status;


    public DeviceKeepLive(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }
}
