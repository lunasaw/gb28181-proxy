package io.github.lunasaw.gbproxy.server.transimit.response.invite;

import java.text.ParseException;

import javax.sip.ResponseEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

import org.springframework.stereotype.Component;

import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.gbproxy.server.transimit.cmd.ServerSendCmd;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.service.SipUserGenerate;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 发起INVITE响应
 *
 * @author luna
 */
@Slf4j
@Getter
@Setter
@Component
public class InviteResponseProcessor extends SipResponseProcessorAbstract {

    private static final String METHOD = "INVITE";

    private String method = METHOD;

    public InviteProcessorServer inviteProcessorServer;

    public InviteResponseProcessor(InviteProcessorServer inviteProcessorServer) {
        this.inviteProcessorServer = inviteProcessorServer;
    }

    /**
     * 处理invite响应
     *
     * @param evt 响应消息
     * @throws ParseException
     */
    @Override
    public void process(ResponseEvent evt) {
        try {
            SIPResponse response = (SIPResponse) evt.getResponse();
            int statusCode = response.getStatusCode();
            if (statusCode == Response.TRYING) {
                responseTrying();
            }

            if (statusCode == Response.OK) {
                ResponseEventExt event = (ResponseEventExt) evt;
                responseAck(event);
            }
        } catch (Exception e) {
            log.info("[点播回复ACK]，异常：", e);
        }
    }

    public void responseTrying() {
        // trying不会回复

    }

    public void responseAck(ResponseEventExt evt) {
        // 成功响应
        SIPResponse response = (SIPResponse) evt.getResponse();

        String toUserId = SipUtils.getUserIdFromToHeader(response);
        String fromUserId = SipUtils.getUserIdFromFromHeader(response);
        CallIdHeader callIdHeader = response.getCallIdHeader();
        FromDevice fromDevice = (FromDevice)inviteProcessorServer.getFromDevice(fromUserId);

        ToDevice toDevice = ToDevice.getInstance(toUserId, evt.getRemoteIpAddress(), evt.getRemotePort());

        // 回复ack
        ServerSendCmd.deviceAck(fromDevice, toDevice, callIdHeader.getCallId());
    }

}
