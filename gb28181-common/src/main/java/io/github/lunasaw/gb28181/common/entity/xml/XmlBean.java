package io.github.lunasaw.gb28181.common.entity.xml;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/12
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.NONE)
public class XmlBean {

    /**
     * 字符集, 支持 UTF-8 与 GB2312
     */
    private String charset = "UTF-8";

    @SneakyThrows
    @Override
    public String toString() {
        JAXBContext jaxbContext = JAXBContext.newInstance(this.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);

        StringWriter writer = new StringWriter();
        marshaller.marshal(this, writer);
        return writer.toString();
    }

    @SneakyThrows
    public static  <T> Object parseObj(String xmlStr, Class<T> clazz) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(new StringReader(xmlStr));
    }
}
