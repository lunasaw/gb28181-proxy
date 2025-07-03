package io.github.lunasaw.gbproxy.server.transimit.request.register;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
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
import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.GbSipDate;
import io.github.lunasaw.sip.common.entity.RemoteAddressInfo;
import io.github.lunasaw.sip.common.entity.SipTransaction;
import io.github.lunasaw.sip.common.transmit.ResponseCmd;
import io.github.lunasaw.sip.common.transmit.event.SipMethod;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
import io.github.lunasaw.sip.common.utils.SipRequestUtils;
import io.github.lunasaw.sip.common.utils.SipUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * REGISTER请求处理器
 * 处理服务端接收到的设备注册请求
 *
 * @author luna
 */
@SipMethod("REGISTER")
@Getter
@Setter
@Component
@Slf4j
public class ServerRegisterRequestProcessor extends SipRequestProcessorAbstract {

    @Autowired
    private RegisterProcessorServer registerProcessorServer;

    @Autowired
    private SipUserGenerateServer   sipUserGenerate;

    /**
     * 收到注册请求 处理
     *
     * @param evt
     */
    @Override
    public void process(RequestEvent evt) {
        try {
            SIPRequest request = (SIPRequest)evt.getRequest();
            // 注册/注销
            int expires = request.getExpires().getExpires();
            boolean registerFlag = expires > 0;

            String userId = SipUtils.getUserIdFromFromHeader(request);

            FromDevice fromDevice = (FromDevice)sipUserGenerate.getFromDevice();
            if (fromDevice == null) {
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
            ViaHeader reqViaHeader = (ViaHeader)request.getHeader(ViaHeader.NAME);
            String transport = reqViaHeader.getTransport();
            registerInfo.setTransport(transport);
            registerInfo.setLocalIp(receiveIp);
            registerInfo.setRemoteAddress(requestAddress);

            if (registerFlag) {
                registerProcessorServer.registerSuccess(userId, registerInfo);
            } else {
                registerProcessorServer.registerOut(userId, registerInfo);
            }

            // 回复200 OK
            ResponseCmd.doResponseCmd(Response.OK, "OK", evt);

        } catch (Exception e) {
            log.error("process register request error", e);
            ResponseCmd.doResponseCmd(Response.SERVER_INTERNAL_ERROR, "Internal Server Error", evt);
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
