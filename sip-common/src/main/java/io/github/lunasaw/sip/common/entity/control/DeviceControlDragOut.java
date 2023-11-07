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
 * <?xml version="1.0" encoding="GB2312"?>
 * <Control>
 * <CmdType>DeviceControl</CmdType>
 * <SN>331004</SN>
 * <DeviceID>1231</DeviceID>
 * <DragZoomOut>
 * <Length>dragZoom.getLength()</Length>
 * <Width>dragZoom.getWidth()</Width>
 * <MidPointX>ragZoom.getMidPointX()</MidPointX>
 * <MidPointY> dragZoom.getMidPointY()</MidPointY>
 * <LengthX>ragZoom.getLengthX()</LengthX>
 * <LengthY> dragZoom.getLengthY()</LengthY>
 * </DragZoomOut>
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
public class DeviceControlDragOut extends DeviceControlBase {


    /**
     * 缩小
     */
    @XmlElement(name = "DragZoomOut")
    private DragZoom dragZoomOut;

    public DeviceControlDragOut(String cmdType, String sn, String deviceId) {
        super(cmdType, sn, deviceId);
        this.setControlType("DragZoomOut");
    }

    public static void main(String[] args) {
        DeviceControlDragOut deviceControlDrag = new DeviceControlDragOut();

        deviceControlDrag.setDragZoomOut(new DragZoom("1", "2", "3", "4", "5", "6"));

        System.out.println(deviceControlDrag);
    }

}
