package io.github.lunasaw.gbproxy.client.entity;

import lombok.Data;
import lombok.SneakyThrows;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

/**
 * @author luna
 * @date 2023/10/12
 */
@Data
public class XmlBean {

    private String charset = "GB28181";

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
}
