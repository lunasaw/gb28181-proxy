package io.github.lunasaw.gbproxy.client.entity;

import com.google.common.collect.Lists;
import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceCatalog extends XmlBean {
    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public String sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

    @XmlElement(name = "SumNum")
    public int sumNum;

    @XmlElement(name = "Item")
    @XmlElementWrapper(name = "DeviceList")
    public List<DeviceItem> deviceItemList;



    public static void main(String[] args) {
        DeviceCatalog deviceCatalog = new DeviceCatalog();
        deviceCatalog.setDeviceId("123");
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setAddress("!23");
        deviceCatalog.setDeviceItemList(Lists.newArrayList(deviceItem, deviceItem));
        System.out.println(deviceCatalog);
    }
}
