package io.github.lunasaw.gbproxy.server.transimit.request.register;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.sip.RequestEvent;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.luna.common.date.DateUtils;

import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.entity.*;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP命令类型： REGISTER请求
 *
 * @author luna
 */
@Getter
@Setter
@Component("serverRegisterRequestProcessor")
@Slf4j
public class ServerRegisterRequestProcessor extends SipRequestProcessorAbstract {

    public final String METHOD = "REGISTER";

    private String method = METHOD;

    @Resource
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

            FromDevice fromDevice = (FromDevice)registerProcessorServer.getFromDevice();
            if (fromDevice == null || !sipUserId.equals(fromDevice.getUserId())) {
                return;
            }
            // 设备接收到的IP地址，有可能是Nat之后的, 本地回复直接使用这个地址即可
            String receiveIp = request.getLocalAddress().getHostAddress();
            // 设备发送请求的地址。主动发送需要nat转换后的地址
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
            registerInfo.setLocalIp(receiveIp);
            registerInfo.setRemotePort(remoteAddressInfo.getPort());
            registerInfo.setRemoteIp(remoteAddressInfo.getIp());

            SipTransaction sipTransaction = SipUtils.getSipTransaction(request);
            if (!registerFlag) {
                log.info(title + "设备：{}, 设备注销: {}", userId, expires);
                registerProcessorServer.deviceOffLine(userId, registerInfo, sipTransaction);
                return;
            }

            registerProcessorServer.updateRegisterInfo(userId, registerInfo);
            String callId = SipUtils.getCallId(request);
            List<Header> okHeaderList = getRegisterOkHeaderList(request);


            if (transaction != null && callId.equals(transaction.getCallId())) {
                log.info(title + "设备：{}, 注册续订: {}", userId, expires);

                ResponseCmd.doResponseCmd(Response.OK, "OK", receiveIp, request, okHeaderList);
                registerProcessorServer.updateSipTransaction(userId, sipTransaction);
                return;
            }

            String password = StringUtils.EMPTY;
            if (StringUtils.isNotBlank(fromDevice.getPassword())) {
                password = fromDevice.getPassword();
            }

            AuthorizationHeader authHead = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
            if (authHead == null && StringUtils.isNotBlank(password)) {

                // 认证密码不是空, 但是请求头中没有AuthorizationHeader
                log.info(title + " 设备：{}, 回复401: {}", userId, requestAddress);

                String nonce = DigestServerAuthenticationHelper.generateNonce();
                WWWAuthenticateHeader wwwAuthenticateHeader =
                        SipRequestUtils.createWWWAuthenticateHeader(DigestServerAuthenticationHelper.DEFAULT_SCHEME, fromDevice.getRealm(), nonce,
                                DigestServerAuthenticationHelper.DEFAULT_ALGORITHM);

                ResponseCmd.doResponseCmd(Response.UNAUTHORIZED, "Unauthorized", receiveIp, request, wwwAuthenticateHeader);
                return;
            }

            // 校验密码是否正确
            boolean passwordCorrect = ObjectUtils.isEmpty(password) ||
                    DigestServerAuthenticationHelper.doAuthenticatePlainTextPassword(request, password);

            if (!passwordCorrect) {
                // 注册失败
                log.info(title + " 设备：{}, 密码/SIP服务器ID错误, 回复403: {}", userId, requestAddress);
                ResponseCmd.doResponseCmd(Response.FORBIDDEN, "wrong password", receiveIp, request);
                return;
            }

            // 携带授权头并且密码正确
            ResponseCmd.doResponseCmd(Response.OK, "OK", receiveIp, request, okHeaderList);
            // 注册成功
            registerProcessorServer.updateSipTransaction(userId, sipTransaction);
        } catch (Exception e) {
            log.error("未处理的异常 ", e);
        }
    }

    private List<Header> getRegisterOkHeaderList(Request request) {

        List<Header> list = Lists.newArrayList();
        // 添加date头
        SIPDateHeader dateHeader = new SIPDateHeader();
        // 使用自己修改的
        GbSipDate gbSipDate = new GbSipDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
        dateHeader.setDate(gbSipDate);
        list.add(dateHeader);

        // 添加Contact头
        list.add(request.getHeader(ContactHeader.NAME));
        // 添加Expires头
        list.add(request.getExpires());
        return list;

    }

}
