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
public class DeviceControlDragIn extends DeviceControlBase {

    /**
     * 放大
     */
    @XmlElement(name = "DragZoomIn")
    private DragZoom dragZoomIn;


    public DeviceControlDragIn(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
        this.setControlType("DragZoomIn");
    }

    public static void main(String[] args) {
        DeviceControlDragIn deviceControlDrag = new DeviceControlDragIn();

        deviceControlDrag.setDragZoomIn(new DragZoom("1", "2", "3", "4", "5", "6"));

        System.out.println(deviceControlDrag);
    }


}
