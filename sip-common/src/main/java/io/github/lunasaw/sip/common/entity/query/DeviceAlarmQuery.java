package io.github.lunasaw.sip.common.entity.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.Getter;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="gb2312"?>
 * <Query>
 * <CmdType>Alarm</CmdType>
 * <SN>217408</SN>
 * <DeviceID>123</DeviceID>
 * <StartAlarmPriority>12312</StartAlarmPriority>
 * <EndAlarmPriority>123</EndAlarmPriority>
 * <AlarmMethod>alarmMethod</AlarmMethod>
 * <AlarmType>alarmType</AlarmType>
 * <StartAlarmTime>startTime</StartAlarmTime>
 * <EndAlarmTime>endTime</EndAlarmTime>
 * </Query>
 * 
 * @author luna
 */
@Getter
@Setter
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceAlarmQuery extends XmlBean {
    @XmlElement(name = "CmdType")
    public String cmdType;

    @XmlElement(name = "SN")
    public String sn;

    @XmlElement(name = "DeviceID")
    public String deviceId;

    @XmlElement(name = "StartTime")
    public String startTime;

    @XmlElement(name = "EndTime")
    public String endTime;

    @XmlElement(name = "StartAlarmPriority")
    public String startAlarmPriority;

    /**
     * 
     */
    @XmlElement(name = "endAlarmPriority")
    public String endAlarmPriority;

    @XmlElement(name = "AlarmType")
    public String alarmType;

    public DeviceAlarmQuery() {}

    public DeviceAlarmQuery(String cmdType, String sn, String deviceId) {
        this.cmdType = cmdType;
        this.sn = sn;
        this.deviceId = deviceId;
    }

    public static void main(String[] args) {

    }
}
