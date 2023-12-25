package io.github.lunasaw.gbproxy.client.transmit.request.subscribe.catalog;

import javax.sip.RequestEvent;
import javax.sip.header.ContentTypeHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.gb28181.common.entity.response.DeviceSubscribe;
import io.github.lunasaw.sip.common.enums.ContentTypeEnum;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.gbproxy.client.transmit.request.subscribe.SubscribeClientHandlerAbstract;
import io.github.lunasaw.gbproxy.client.transmit.request.subscribe.SubscribeProcessorClient;
import io.github.lunasaw.gb28181.common.entity.base.DeviceSession;
import io.github.lunasaw.gb28181.common.entity.query.DeviceQuery;
import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.transmit.event.message.MessageHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 处理设备通道订阅消息 回复OK
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class CatalogQueryMessageHandler extends SubscribeClientHandlerAbstract {

    public static final String CMD_TYPE = CmdTypeEnum.CATALOG.getType();

    public CatalogQueryMessageHandler(SubscribeProcessorClient subscribeProcessorClient) {
        super(subscribeProcessorClient);
    }

    @Override
    public String getRootType() {
        return MessageHandler.QUERY;
    }

    @Override
    public void handForEvt(RequestEvent event) {
        DeviceSession deviceSession = getDeviceSession(event);
        // 订阅消息过来
        String sipId = deviceSession.getSipId();
        String userId = deviceSession.getUserId();
        SIPRequest request = (SIPRequest)event.getRequest();
        SubscribeInfo subscribeInfo = new SubscribeInfo(request, sipId);
        Device fromDevice = subscribeProcessorClient.getFromDevice();
        if (!userId.equals(fromDevice.getUserId())) {
            return;
        }

        DeviceQuery deviceQuery = parseXml(DeviceQuery.class);
        subscribeProcessorClient.putSubscribe(deviceQuery.getDeviceId(), subscribeInfo);

        DeviceSubscribe deviceSubscribe = subscribeProcessorClient.getDeviceSubscribe(deviceQuery);
        ExpiresHeader expiresHeader = SipRequestUtils.createExpiresHeader(subscribeInfo.getExpires());

        ContentTypeHeader contentTypeHeader = ContentTypeEnum.APPLICATION_XML.getContentTypeHeader();
        ResponseCmd.doResponseCmd(Response.OK, deviceSubscribe.toString(), contentTypeHeader, event, expiresHeader);
    }

    @Override
    public String getCmdType() {
        return CMD_TYPE;
    }

    @Override
    public boolean needResponseAck() {
        return false;
    }
}
