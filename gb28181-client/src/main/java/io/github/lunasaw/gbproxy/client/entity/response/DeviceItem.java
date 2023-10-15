package io.github.lunasaw.gbproxy.client.entity.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

import com.luna.common.check.Assert;
import com.luna.common.date.DateUtils;
import com.luna.common.os.SystemInfoUtil;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import io.github.lunasaw.sip.common.enums.DeviceGbType;
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
    /**
     * 设备/区域/系(必选)
     * 通道ID
     */
    @XmlElement(name = "DeviceID")
    private String deviceId;
    /**
     * 设备/区域/系(必选)
     */
    @XmlElement(name = "Name")
    private String name;
    /**
     * 当为设备时,设备厂商(必选)
     */
    @XmlElement(name = "Manufacturer")
    private String manufacturer;
    /**
     * 当为设备时,设备型号(必选)
     */
    @XmlElement(name = "Model")
    private String model;
    /**
     * 当为设备时,设备归属(必选)
     */
    @XmlElement(name = "Owner")
    private String owner;
    /**
     * 行政区域(必选)
     */
    @XmlElement(name = "CivilCode")
    private String civilCode;
    /**
     * 警区(可选)
     */
    @XmlElement(name = "Block")
    private String block;
    /**
     * 当为设备时,安装地址(必选)
     */
    @XmlElement(name = "Address")
    private String address;
    /**
     * 当为设备时,是否有子设备 (必选)1有,0没有
     */
    @XmlElement(name = "Parental")
    private int parental;
    /**
     * 父设备/区域/系统ID(必选)
     */
    @XmlElement(name = "ParentID")
    private String parentId;
    /**
     * 业务分组
     */
    @XmlElement(name = "BusinessGroupID")
    private String businessGroupId;
    /**
     * 信令安全模式(可选)缺省为0:不采用;2:S/MIME 签名方式;3:S/ MIME加密签名同时采用方式;4:数字摘要方式
     */
    @XmlElement(name = "SafetyWay")
    private int safetyWay;
    /**
     * 注册方式(必选)缺省为1;1:符合IETFRFC3261标准的认证注册模 式;2:基于口令的双向认证注册模式;3:基于数字证书的双向认证注册模式
     */
    @XmlElement(name = "RegisterWay")
    private int registerWay;
    /**
     * 证书序列号(有证书的设备必选)
     */
    @XmlElement(name = "CertNum")
    private String certNum;
    /**
     * 证书有效标识(有证书的设备必选) 缺省为0;证书有效标识:0:无效 1: 有效
     */
    @XmlElement(name = "Certifiable")
    private int certifiable;
    /**
     * 无效原因码(有证书且证书无效的设备必选)
     */
    @XmlElement(name = "ErrCode")
    private int errCode;
    /**
     * 证书终止有效期(有证书的设备必选)
     */
    @XmlElement(name = "EndTime")
    private String endTime;
    /**
     * 保密属性(必选)缺省为0 :不涉密,1:涉密
     */
    @XmlElement(name = "Secrecy")
    private int secrecy;
    /**
     * 设备/区域/系统IP地址(可选)
     */
    @XmlElement(name = "IPAddress")
    private String ipAddress;
    /**
     * 设备/区域/系统端口(可选)
     */
    @XmlElement(name = "Port")
    private int port;
    /**
     * 设备口令(可选)
     */
    @XmlElement(name = "Password")
    private String password;
    /**
     * 云台类型(可选)
     */
    @XmlElement(name = "PTZType")
    private int    ptzType;
    /**
     * 设备状态(必选)
     */
    @XmlElement(name = "Status")
    private String status;
    /**
     * 经度(可选)
     */
    @XmlElement(name = "Longitude")
    private double longitude;
    /**
     * 纬度(可选)
     */
    @XmlElement(name = "Latitude")
    private double latitude;

    public static DeviceItem getInstanceExample(String deviceId) {
        Assert.notNull(deviceId, "设备ID不能为空");
        Assert.isTrue(deviceId.length() == 20, "设备ID长度必须为20位");

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setName("Camera");
        deviceItem.setManufacturer("Lunasaw");
        deviceItem.setModel("Model-2312");
        deviceItem.setOwner("luna");
        if (StringUtils.isNotBlank(deviceId)) {
            deviceItem.setCivilCode(deviceId.substring(0, 6));
        }

        String substring = deviceId.substring(10, 13);

        DeviceGbType deviceGbType = DeviceGbType.fromCode(Integer.parseInt(substring));

        deviceItem.setBlock("block");
        deviceItem.setAddress("上海市xxx区xxx接到");
        deviceItem.setCertifiable(0);
        deviceItem.setErrCode(500);
        deviceItem.setEndTime(DateUtils.formatTime(DateUtils.ISO8601_PATTERN, DateUtils.parseDate("2099-01-01 01:01:01")));
        deviceItem.setSecrecy(0);
        deviceItem.setSafetyWay(0);
        deviceItem.setIpAddress(SystemInfoUtil.getNoLoopbackIP());
        deviceItem.setPort(8116);
        deviceItem.setPassword("luna");
        deviceItem.setPtzType(3);
        deviceItem.setStatus("ok");
        deviceItem.setLongitude(121.472644);
        deviceItem.setLatitude(31.231706);

        if (DeviceGbType.CENTER_SERVER.equals(deviceGbType)) {
            deviceItem.setRegisterWay(1);
            deviceItem.setSecrecy(0);
        }
        if (DeviceGbType.VIRTUAL_ORGANIZATION_DIRECTORY.equals(deviceGbType)) {
            deviceItem.setParentId("0");
        }
        if (DeviceGbType.CENTER_SIGNAL_CONTROL_SERVER.equals(deviceGbType)) {
            deviceItem.setParentId("0");
            deviceItem.setBusinessGroupId("0");
        }

        return deviceItem;
    }

    public static void main(String[] args) throws Exception {

        DeviceItem deviceItem = new DeviceItem();
        deviceItem.setDeviceId("12312312");

        System.out.println(deviceItem);
    }
}