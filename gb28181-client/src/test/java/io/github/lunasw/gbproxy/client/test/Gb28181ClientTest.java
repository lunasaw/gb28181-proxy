package io.github.lunasw.gbproxy.client.test;

import io.github.lunasaw.gbproxy.client.Gb28181Client;
import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * @author luna
 * @date 2023/10/12
 */
@SpringBootTest(classes = Gb28181Client.class)
public class Gb28181ClientTest {

    @Test
    public void atest() throws Exception {
        File file = ResourceUtils.getFile("classpath:device/catalog.xml");
        List<String> catalogList = Files.readAllLines(file.toPath());
        StringBuffer catalogXml = new StringBuffer();
        String sn = "34020000002000000001";
        String deviceId = "34020000001320000001";
        for (String xml : catalogList) {
            catalogXml.append(xml.replaceAll("\\$\\{SN\\}", sn).replaceAll("\\$\\{DEVICE_ID\\}", deviceId)).append("\r\n");
        }

        System.out.println(catalogXml.toString());
    }

    @Test
    public void btest() throws Exception {
        SAXReader reader = new SAXReader();
        File file = ResourceUtils.getFile("classpath:device/catalog.xml");

        Document document = reader.read(file);
        Element root = document.getRootElement();
//        Item item = new Item();
//        item.setDeviceId(root.elementText("DeviceID"));
//        item.setName(root.elementText("Name"));
//        item.setManufacturer(root.elementText("Manufacturer"));

        QName qName = root.getQName();
        System.out.println(qName);

        Element cmdType = root.element("CmdType");
        System.out.println(cmdType.getText());

        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(System.out, format);
        writer.write( document );

        // Compact format to System.out
        format = OutputFormat.createCompactFormat();
        writer = new XMLWriter(System.out, format);
        writer.write(document);
        writer.close();
    }
}
