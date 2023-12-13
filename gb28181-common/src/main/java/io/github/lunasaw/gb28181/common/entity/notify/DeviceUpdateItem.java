package io.github.lunasaw.gb28181.common.entity.notify;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.gb28181.common.entity.response.DeviceCatalog;
import lombok.Getter;
import lombok.Setter;

/**
 * @author luna
 * @date 2023/10/15
 */
@Getter
@Setter
@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceUpdateItem extends DeviceCatalog {

    @XmlElement(name = "Event")
    private String event;

    public static void main(String[] args) {

    }
}
