package io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control;

import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.service.SipUserGenerate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.message.MessageProcessorClient;
import io.github.lunasaw.gbproxy.client.transmit.request.message.handler.control.emums.DeviceControlType;
import io.github.lunasaw.sip.common.utils.SpringBeanFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class DeviceControlMessageHandler extends MessageClientHandlerAbstract {

    public static final String CMD_TYPE = "DeviceControl";

    private String             cmdType  = CMD_TYPE;

    @Autowired
    private SpringBeanFactory  springBeanFactory;

    public DeviceControlMessageHandler(MessageProcessorClient messageProcessorClient, SipUserGenerate sipUserGenerate) {
        super(messageProcessorClient, sipUserGenerate);
    }


    @Override
    public String getRootType() {
        return CONTROL;
    }

    @Override
    public void handForEvt(RequestEvent event) {

        String xmlStr = getXmlStr();
        DeviceControlType deviceControlType = DeviceControlType.getDeviceControlTypeFilter(xmlStr);
        if (deviceControlType == null) {
            return;
        }
        Object o = parseXml(deviceControlType.getClazz());

        DeviceControlCmd bean = SpringBeanFactory.getBean(deviceControlType.getBeanName());

        if (bean == null) {
            log.info("handForEvt::event = {}", xmlStr);
            return;
        }
        bean.doCmd(o);
    }

    @Override
    public String getCmdType() {
        return cmdType;
    }
}
