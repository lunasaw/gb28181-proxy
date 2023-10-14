package io.github.lunasaw.gbproxy.server.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.*;

/**
 *
 * /**
 * <?xml version="1.0" encoding="gb2312"?>
 * <Control>
 * <CmdType>DeviceControl</CmdType>
 * <SN>179173</SN>
 * <DeviceID>213</DeviceID>
 * <AlarmCmd>ResetAlarm</AlarmCmd>
 * <Info>
 * <AlarmMethod>123</AlarmMethod>
 * <AlarmType>alarmType</AlarmType>
 * </Info>
 * </Control>
 * 
 * @author luna
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "Control")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceControlAlarm extends XmlBean {
    @XmlElement(name = "CmdType")
    public String    cmdType;

    @XmlElement(name = "SN")
    public String    sn;

    @XmlElement(name = "DeviceID")
    public String    deviceId;

    @XmlElement(name = "AlarmCmd")
    public String    alarmCmd;

    @XmlElement(name = "Info")
    public AlarmInfo info;

    public static void main(String[] args) {
        DeviceControlAlarm alarm = new DeviceControlAlarm();
        alarm.setCmdType("DeviceControl");
        alarm.setSn("179173");
        alarm.setDeviceId("123");
        alarm.setAlarmCmd("ResetAlarm");

        AlarmInfo alarmInfo = new AlarmInfo();
        alarmInfo.setAlarmMethod("!231");
        alarmInfo.setAlarmType("alarmType");
        alarm.setInfo(alarmInfo);

        System.out.println(alarm);

    }

    @SneakyThrows
    @Override
    public String toString() {
        return super.toString();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlRootElement(name = "Info")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AlarmInfo {
        @XmlElement(name = "AlarmMethod")
        public String alarmMethod;

        @XmlElement(name = "AlarmType")
        public String alarmType;
    }
}
