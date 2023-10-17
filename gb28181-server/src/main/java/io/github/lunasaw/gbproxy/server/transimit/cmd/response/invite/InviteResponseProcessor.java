package io.github.lunasaw.gbproxy.server.transimit.cmd.response.invite;

import java.text.ParseException;

import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.ResponseEvent;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;

import io.github.lunasaw.gbproxy.server.transimit.cmd.ServerSendCmd;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.ResponseEventExt;
import gov.nist.javax.sip.message.SIPResponse;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.SdpSessionDescription;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 发起INVITE响应
 *
 * @author weidian
 */
@Component
@Slf4j
@Data
public class InviteResponseProcessor extends SipResponseProcessorAbstract {

    private static final String METHOD = "INVITE";

    private String method = METHOD;

    private FromDevice fromDevice;

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

    public void responseAck(ResponseEventExt evt) throws SdpParseException {
        // 成功响应
        SIPResponse response = (SIPResponse) evt.getResponse();

        String contentString = new String(response.getRawContent());
        SdpSessionDescription sessionDescription = SipUtils.parseSdp(contentString);
        SessionDescription sdp = sessionDescription.getBaseSdb();

        String username = sdp.getOrigin().getUsername();

        ToDevice toDevice = ToDevice.getInstance(username, evt.getRemoteIpAddress(), evt.getRemotePort());
        CallIdHeader callIdHeader = response.getCallIdHeader();

        // 回复ack
        ServerSendCmd.deviceAck(fromDevice, toDevice, callIdHeader.getCallId());
    }

}
