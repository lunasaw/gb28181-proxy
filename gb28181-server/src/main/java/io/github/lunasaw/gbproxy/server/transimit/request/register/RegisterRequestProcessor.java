package io.github.lunasaw.gbproxy.server.transimit.request.register;

import java.util.Calendar;
import java.util.Locale;

import javax.sip.RequestEvent;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ViaHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import io.github.lunasaw.sip.common.transmit.response.SipResponseProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.luna.common.date.DateUtils;

import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.*;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP命令类型： REGISTER请求
 *
 * @author weidian
 */
@Getter
@Setter
@Component
@Slf4j
public class RegisterRequestProcessor extends SipRequestProcessorAbstract {

    public final String METHOD = "REGISTER";

    private String method = METHOD;

    private RegisterProcessorServer registerProcessorServer;

    /**
     * 收到注册请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        try {
            SIPRequest request = (SIPRequest) evt.getRequest();
            // 注册/注销
            int expires = request.getExpires().getExpires();
            boolean registerFlag = expires > 0;

            String userId = SipUtils.getUserIdFromFromHeader(request);
            String sipUserId = SipUtils.getUserIdFromToHeader(request);

            ToDevice toDevice = (ToDevice) registerProcessorServer.getToDevice(userId);
            FromDevice fromDevice = (FromDevice) registerProcessorServer.getFromDevice(sipUserId);

            RemoteAddressInfo remoteAddressInfo = SipUtils.getRemoteAddressFromRequest(request);
            String requestAddress = remoteAddressInfo.getIp() + ":" + remoteAddressInfo.getPort();

            String title = registerFlag ? "[注册请求]" : "[注销请求]";
            log.info(title + "设备：{}, 开始处理: {}", userId, requestAddress);

            SipTransaction transaction = registerProcessorServer.getTransaction(userId);

            RegisterInfo registerInfo = new RegisterInfo();

            registerInfo.setExpire(expires);
            registerInfo.setRegisterTime(DateUtils.getCurrentDate());
            // 判断TCP还是UDP
            ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
            String transport = reqViaHeader.getTransport();
            registerInfo.setTransport("TCP".equalsIgnoreCase(transport) ? "TCP" : "UDP");
            registerInfo.setLocalIp(request.getLocalAddress().getHostAddress());
            registerProcessorServer.updateRegisterInfo(userId, registerInfo);

            SipTransaction sipTransaction = SipUtils.getSipTransaction(request);
            registerProcessorServer.updateSipTransaction(userId, sipTransaction);

            String callId = SipUtils.getCallId(request);
            if (toDevice != null && transaction != null && callId.equals(transaction.getCallId())) {
                log.info(title + "设备：{}, 注册续订: {}", userId, expires);
                //
                Response registerOkResponse = getRegisterOkResponse(request);
                SipSender.transmitRequest(request.getLocalAddress().getHostAddress(), registerOkResponse);
                return;
            }

            String password = StringUtils.EMPTY;
            if (fromDevice != null && StringUtils.isNotBlank(fromDevice.getPassword())) {
                password = fromDevice.getPassword();
            }

            AuthorizationHeader authHead = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
            if (authHead == null && StringUtils.isNotBlank(password)) {

                // 认证密码不是空, 但是请求头中没有AuthorizationHeader

                log.info(title + " 设备：{}, 回复401: {}", userId, requestAddress);


                Response response = SipRequestUtils.createResponse(Response.UNAUTHORIZED, request);

                String nonce = DigestServerAuthenticationHelper.generateNonce();
                WWWAuthenticateHeader wwwAuthenticateHeader =
                        SipRequestUtils.createWWWAuthenticateHeader(DigestServerAuthenticationHelper.DEFAULT_SCHEME, fromDevice.getRealm(), nonce,
                                DigestServerAuthenticationHelper.DEFAULT_ALGORITHM);
                // okResponse.addHeader(wwwAuthenticateHeader);
                response.addHeader(wwwAuthenticateHeader);
                SipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);

                return;
            }

            // 校验密码是否正确
            boolean passwordCorrect = ObjectUtils.isEmpty(password) ||
                    DigestServerAuthenticationHelper.doAuthenticatePlainTextPassword(request, password);

            if (!passwordCorrect) {
                // 注册失败
                Response response = SipRequestUtils.createResponse(Response.FORBIDDEN, request);
                response.setReasonPhrase("wrong password");
                log.info(title + " 设备：{}, 密码/SIP服务器ID错误, 回复403: {}", userId, requestAddress);

                SipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
                return;
            }

            // 携带授权头并且密码正确
            Response registerOkResponse = getRegisterOkResponse(request);
            SipSender.transmitRequest(request.getLocalAddress().getHostAddress(), registerOkResponse);
            // 注册成功
        } catch (Exception e) {
            log.error("未处理的异常 ", e);
        }
    }

    private Response getRegisterOkResponse(Request request) {
        // 携带授权头并且密码正确
        Response response = SipRequestUtils.createResponse(Response.OK, request);
        // 添加date头
        SIPDateHeader dateHeader = new SIPDateHeader();
        // 使用自己修改的
        GbSipDate gbSipDate = new GbSipDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
        dateHeader.setDate(gbSipDate);
        response.addHeader(dateHeader);

        // 添加Contact头
        response.addHeader(request.getHeader(ContactHeader.NAME));
        // 添加Expires头
        response.addHeader(request.getExpires());
        return response;

    }

    public void processRegister(RequestEvent evt) {
        // 获取userId
    }
}
