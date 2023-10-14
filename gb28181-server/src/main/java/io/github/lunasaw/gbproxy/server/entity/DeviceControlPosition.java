package io.github.lunasaw.gbproxy.server.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.xml.XmlBean;
import lombok.*;

/**
 *
 * <?xml version="1.0" encoding="gb2312"?>
 * <Control>
 * <CmdType>DeviceControl</CmdType>
 * <SN>840481</SN>
 * <DeviceID>channelId</DeviceID>
 * <HomePosition>
 * <Enabled>1</Enabled>
 * <ResetTime>resetTime</ResetTime>
 * <PresetIndex>presetIndex</PresetIndex>
 * </HomePosition>
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
public class DeviceControlPosition extends XmlBean {
    @XmlElement(name = "CmdType")
    public String    cmdType;

    @XmlElement(name = "SN")
    public String    sn;

    @XmlElement(name = "DeviceID")
    public String    deviceId;

    @XmlElement(name = "AlarmCmd")
    public String    alarmCmd;

    @XmlElement(name = "HomePosition")
    public HomePosition homePosition;

    public static void main(String[] args) {
        DeviceControlPosition alarm = new DeviceControlPosition();
        alarm.setCmdType("DeviceControl");
        alarm.setSn("179173");
        alarm.setDeviceId("123");

        HomePosition homePosition = new HomePosition();
        homePosition.setEnabled("1");
        homePosition.setResetTime("222");
        homePosition.setPresetIndex("2");
        alarm.setHomePosition(homePosition);

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
    @XmlRootElement(name = "HomePosition")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class HomePosition {
        @XmlElement(name = "Enabled")
        public String enabled;

        @XmlElement(name = "ResetTime")
        public String resetTime;

        @XmlElement(name = "PresetIndex")
        private String presetIndex;
    }
}
