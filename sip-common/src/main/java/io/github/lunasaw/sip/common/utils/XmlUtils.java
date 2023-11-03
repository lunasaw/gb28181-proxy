package io.github.lunasaw.sip.common.utils;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.util.ResourceUtils;

import com.google.common.base.Joiner;

import lombok.SneakyThrows;

/**
 * @author luna
 * @date 2023/10/15
 */
public class XmlUtils {

    @SneakyThrows
    public static String toString(String charset, Object object) {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, charset);

        StringWriter writer = new StringWriter();
        marshaller.marshal(object, writer);
        return writer.toString();
    }

    @SneakyThrows
    public static <T> Object parseObj(String xmlStr, Class<T> clazz) {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return unmarshaller.unmarshal(new StringReader(xmlStr));
    }

    @SneakyThrows
    public static <T> Object parseFile(String resource, Class<T> clazz) {
        File file = ResourceUtils.getFile(resource);
        List<String> strings = Files.readAllLines(Paths.get(file.getAbsolutePath()));

        String join = Joiner.on("\n").join(strings);
        return parseObj(join, clazz);
    }

    @SneakyThrows
    public static String getCmdType(String xmlStr) {
        SAXReader reader = new SAXReader();

        Document document = reader.read(new StringReader(xmlStr));
        // 获取根元素
        Element root = document.getRootElement();
        // 获取CmdType子元素
        Element cmdType = root.element("CmdType");

        return cmdType.getText();
    }

}
