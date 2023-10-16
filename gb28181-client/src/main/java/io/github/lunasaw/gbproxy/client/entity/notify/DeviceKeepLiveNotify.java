package io.github.lunasaw.gbproxy.client.entity.notify;

import javax.xml.bind.annotation.*;

import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Notify")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceKeepLiveNotify extends DeviceBase {


    @XmlElement(name = "Status")
    public String status;


    public DeviceKeepLiveNotify(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }

    public static void main(String[] args) {
        DeviceKeepLiveNotify deviceKeepLiveNotify = new DeviceKeepLiveNotify();
        deviceKeepLiveNotify.setStatus("OK");
        System.out.println(deviceKeepLiveNotify);
    }
}
