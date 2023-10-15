package io.github.lunasaw.gbproxy.client.entity.response;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.Getter;
import lombok.Setter;
import org.assertj.core.util.Lists;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <?xml version="1.0"?>
 * <Response>
 *     <CmdType>Catalog</CmdType>
 *     <SN>${SN}</SN>
 *     <DeviceID>${DEVICE_ID}</DeviceID>
 *     <SumNum>1</SumNum>
 *     <DeviceList>
 *         <Item>
 *             <DeviceID>33010602011187000002</DeviceID>
 *             <Name>Camera</Name>
 *             <Manufacturer>海康</Manufacturer>
 *             <Model>Model</Model>
 *             <Owner>Owner</Owner>
 *             <CivilCode>CivilCode</CivilCode>
 *             <Block>Block</Block>
 *             <Address>上海市五角场合生汇</Address>
 *             <Parental>0</Parental>
 *             <ParentID>${DEVICE_ID}</ParentID>
 *             <SafetyWay>0</SafetyWay>
 *             <RegisterWay>1</RegisterWay>
 *             <CertNum>CertNum1</CertNum>
 *             <Certifiable>0</Certifiable>
 *             <ErrCode>400</ErrCode>
 *             <EndTime>2010-11-11T19:46:17</EndTime>
 *             <Secrecy>0</Secrecy>
 *             <IPAddress>172.19.128.50</IPAddress>
 *             <Port>5060</Port>
 *             <Password>Password1</Password>
 *             <PTZType>1</PTZType>
 *             <Status>Status1</Status>
 *             <Longitude>171.4</Longitude>
 *             <Latitude>34.2</Latitude>
 *         </Item>
 *     </DeviceList>
 * </Response>
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceChanel extends XmlBean {
    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public String sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

    @XmlElement(name = "SumNum")
    public int sumNum;

    @XmlElement(name = "DeviceList")
    private DeviceList deviceList;

    public static void main(String[] args) {
        DeviceChanel deviceChanel = new DeviceChanel();

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setDeviceId("12312312");

        DeviceList deviceList1 = new DeviceList();

        deviceList1.setDeviceItemList(Lists.newArrayList(deviceItem, deviceItem));

        deviceChanel.setDeviceList(deviceList1);
        System.out.println(deviceChanel);
    }

    @Getter
    @Setter
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DeviceList{

        @XmlElement(name = "Item")
        public List<DeviceItem> deviceItemList;

        @XmlAttribute(name = "Num")
        private String num = "1";
    }
}
