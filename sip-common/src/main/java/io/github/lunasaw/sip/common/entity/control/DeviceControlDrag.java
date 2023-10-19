package io.github.lunasaw.sip.common.entity.control;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="gb2312"?>
 * <Control>
 * <CmdType>DeviceControl</CmdType>
 * <SN>331004</SN>
 * <DeviceID>1231</DeviceID>
 * <DragZoomIn>
 * <Length>dragZoom.getLength()</Length>
 * <Width>dragZoom.getWidth()</Width>
 * <MidPointX>ragZoom.getMidPointX()</MidPointX>
 * <MidPointY> dragZoom.getMidPointY()</MidPointY>
 * <LengthX>ragZoom.getLengthX()</LengthX>
 * <LengthY> dragZoom.getLengthY()</LengthY>
 * </DragZoomIn>
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
public class DeviceControlDrag extends DeviceBase {

    /**
     * 放大
     */
    @XmlElement(name = "DragZoomIn")
    private DragZoom dragZoomIn;

    /**
     * 缩小
     */
    @XmlElement(name = "DragZoomOut")
    private DragZoom dragZoomOut;

    public DeviceControlDrag(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
    }

    public static void main(String[] args) {
        DeviceControlDrag deviceControlDrag = new DeviceControlDrag();

        deviceControlDrag.setDragZoomIn(new DragZoom("1","2","3","4","5","6"));

        System.out.println(deviceControlDrag);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DragZoom {

        @XmlElement(name = "Length")
        public String length;

        @XmlElement(name = "Width")
        public String width;
        @XmlElement(name = "MidPointX")
        public String midPointX;
        @XmlElement(name = "MidPointY")
        public String midPointY;
        @XmlElement(name = "LengthX")
        public String lengthX;
        @XmlElement(name = "LengthY")
        public String lengthY;
    }
}
