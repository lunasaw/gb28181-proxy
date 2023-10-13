package io.github.lunasaw.gbproxy.client.entity.device;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.gbproxy.client.entity.XmlBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceInfo extends XmlBean {
    @XmlElement(name = "CmdType")
    private String cmdType;
    @XmlElement(name = "SN")
    private String sn;
    @XmlElement(name = "DeviceName")
    private String deviceName;
    @XmlElement(name = "DeviceID")
    private String deviceId;

    @XmlElement(name = "Result")
    private String result;
    /**
     * 设备生产商
     */
    @XmlElement(name = "Manufacturer")
    private String manufacturer;
    /**
     * 设备型号(可选)
     */
    @XmlElement(name = "Model")
    private String model;
    /**
     * 设备固件版本(可选)
     */
    @XmlElement(name = "Firmware")
    private String firmware;
    /**
     * 视频输入通道数(可选)
     */
    @XmlElement(name = "Channel")
    private int    channel;

    public static void main(String[] args) {
        DeviceInfo deviceInfo = new DeviceInfo();
        Object o = DeviceInfo.parseObj("<Response>\n" +
                "    <CmdType>DeviceInfo</CmdType>\n" +
                "    <SN>1</SN>\n" +
                "    <DeviceID>34020000001320000001</DeviceID>\n" +
                "    <Result>OK</Result>\n" +
                "    <DeviceName>34020000001320000001</DeviceName>\n" +
                "    <Manufacturer>HIK</Manufacturer>\n" +
                "    <Model>DS-2CD2T20FD-I5</Model>\n" +
                "    <Firmware>V5.5.82 build 180314</Firmware>\n" +
                "    <Channel>1</Channel>\n" +
                "</Response>", DeviceInfo.class);

        System.out.println(o);
    }
}
