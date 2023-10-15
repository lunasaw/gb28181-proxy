package io.github.lunasaw.gbproxy.client.entity.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.Getter;
import lombok.Setter;

/**
 * toString 使用父类方法
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceItem extends XmlBean {
    @XmlElement(name = "DeviceID")
    private String deviceId;

    @XmlElement(name = "Name")
    private String name;

    @XmlElement(name = "Manufacturer")
    private String manufacturer;

    @XmlElement(name = "Model")
    private String model;

    @XmlElement(name = "Owner")
    private String owner;

    @XmlElement(name = "CivilCode")
    private String civilCode;

    @XmlElement(name = "Block")
    private String block;

    @XmlElement(name = "Address")
    private String address;

    @XmlElement(name = "Parental")
    private int parental;

    @XmlElement(name = "ParentID")
    private String parentId;

    @XmlElement(name = "SafetyWay")
    private int safetyWay;

    @XmlElement(name = "RegisterWay")
    private int registerWay;

    @XmlElement(name = "CertNum")
    private String certNum;

    @XmlElement(name = "Certifiable")
    private int certifiable;

    @XmlElement(name = "ErrCode")
    private int errCode;

    @XmlElement(name = "EndTime")
    private String endTime;

    @XmlElement(name = "Secrecy")
    private int secrecy;

    @XmlElement(name = "IPAddress")
    private String ipAddress;

    @XmlElement(name = "Port")
    private int port;

    @XmlElement(name = "Password")
    private String password;

    @XmlElement(name = "PTZType")
    private String pTZType;

    @XmlElement(name = "Status")
    private String status;

    @XmlElement(name = "Longitude")
    private double longitude;

    @XmlElement(name = "Latitude")
    private double latitude;

    public static void main(String[] args) throws Exception {

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setDeviceId("12312312");

        System.out.println(deviceItem);
    }
}