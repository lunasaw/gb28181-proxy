package io.github.lunasaw.gbproxy.client.device;

import com.google.common.collect.Lists;
import io.github.lunasaw.gbproxy.client.entity.XmlBean;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * @author weidian
 */
@Getter
@Setter
@XmlRootElement(name = "DeviceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceResponse extends XmlBean {
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

    @SneakyThrows
    @Override
    public String toString() {
        return super.toString();
    }

    public static void main(String[] args) {
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.setDeviceId("123");
        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setAddress("!23");
        deviceResponse.setDeviceItemList(Lists.newArrayList(deviceItem, deviceItem));
        System.out.println(deviceResponse);
    }
}
