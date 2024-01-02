package io.github.lunasaw.gbproxy.server.transimit.request.message.response;

import javax.sip.RequestEvent;

import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.gb28181.common.entity.response.DeviceInfo;
import io.github.lunasaw.gb28181.common.entity.response.DeviceRecord;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageProcessorServer;
import io.github.lunasaw.gbproxy.server.transimit.request.message.MessageServerHandlerAbstract;
import io.github.lunasaw.gbproxy.server.transimit.request.message.ServerMessageRequestProcessor;
import io.github.lunasaw.sip.common.entity.DeviceSession;
import io.github.lunasaw.sip.common.entity.ToDevice;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 复制类 无实际使用
 * 
 * @author luna
 * @date 2023/10/19
 */
@Component
@Slf4j
@Getter
@Setter
public class DeviceInfoMessageServerHandler extends MessageServerHandlerAbstract {

    public static final String CMD_TYPE = CmdTypeEnum.DEVICE_INFO.getType();

    private String             cmdType  = CMD_TYPE;

    @Override
    public String getRootType() {
        return ServerMessageRequestProcessor.METHOD + RESPONSE;
    }

    public DeviceInfoMessageServerHandler(MessageProcessorServer messageProcessorServer, SipUserGenerateServer sipUserGenerate) {
        super(messageProcessorServer, sipUserGenerate);
    }


    @Override
    public void handForEvt(RequestEvent event) {
        if (preCheck(event)){
            return;
        }
        DeviceSession deviceSession = getDeviceSession(event);
        String userId = deviceSession.getUserId();

        DeviceInfo deviceInfo = parseXml(DeviceInfo.class);


        messageProcessorServer.updateDeviceInfo(userId, deviceInfo);
    }



    @Override
    public String getCmdType() {
        return cmdType;
    }


}
