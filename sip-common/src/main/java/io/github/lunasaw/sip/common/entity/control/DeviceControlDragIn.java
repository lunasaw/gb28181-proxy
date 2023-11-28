package io.github.lunasaw.sip.common.entity.control;

import javax.xml.bind.annotation.*;

import io.github.lunasaw.sip.common.entity.base.DeviceBase;
import io.github.lunasaw.sip.common.utils.XmlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <?xml version="1.0" encoding="UTF-8"?>
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

        String data = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<Control>\n" +
                "    <CmdType>DeviceControl</CmdType>\n" +
                "    <SN>797466</SN>\n" +
                "    <DeviceID>41010500002000000001</DeviceID>\n" +
                "    <ControlType>DragZoomIn</ControlType>\n" +
                "    <DragZoomIn>\n" +
                "        <Length>1</Length>\n" +
                "        <Width>1</Width>\n" +
                "        <MidPointX>1</MidPointX>\n" +
                "        <MidPointY>1</MidPointY>\n" +
                "        <LengthX>1</LengthX>\n" +
                "        <LengthY>1</LengthY>\n" +
                "    </DragZoomIn>\n" +
                "</Control>\n";

        DeviceControlDragIn o = (DeviceControlDragIn) XmlUtils.parseObj(data, DeviceControlDragIn.class);
        System.out.println(o);
    }


}
