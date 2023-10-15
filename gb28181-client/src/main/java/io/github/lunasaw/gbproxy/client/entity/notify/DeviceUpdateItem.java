package io.github.lunasaw.gbproxy.client.entity.notify;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import io.github.lunasaw.gbproxy.client.entity.DeviceCatalog;
import lombok.Getter;
import lombok.Setter;

/**
 * @author weidian
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
