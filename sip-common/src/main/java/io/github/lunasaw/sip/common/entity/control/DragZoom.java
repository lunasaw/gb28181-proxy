package io.github.lunasaw.sip.common.entity.control;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class DragZoom {

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