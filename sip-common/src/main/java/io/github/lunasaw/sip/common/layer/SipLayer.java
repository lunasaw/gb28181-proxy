package io.github.lunasaw.sip.common.layer;

import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.SipStackImpl;
import io.github.lunasaw.sip.common.conf.DefaultProperties;
import io.github.lunasaw.sip.common.conf.msg.StringMsgParserFactory;
import io.github.lunasaw.sip.common.transmit.SipProcessorObserver;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.util.ObjectUtils;

import javax.sip.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author luna
 */
@Slf4j
public class SipLayer {

    private static final Map<String, SipProviderImpl> tcpSipProviderMap = new ConcurrentHashMap<>();
    private static final Map<String, SipProviderImpl> udpSipProviderMap = new ConcurrentHashMap<>();

    @Getter
    private static final List<String> monitorIpList = Lists.newArrayList("0.0.0.0");

    public static SipProviderImpl getUdpSipProvider(String ip) {
        if (ObjectUtils.isEmpty(ip)) {
            return null;
        }
        return udpSipProviderMap.get(ip);
    }

    public static SipProviderImpl getUdpSipProvider() {
        if (udpSipProviderMap.isEmpty()) {
            throw new RuntimeException("ListeningPoint Not Exist");
        }
        return udpSipProviderMap.values().stream().findFirst().get();
    }

    public static SipProviderImpl getTcpSipProvider() {
        if (tcpSipProviderMap.size() != 1) {
            return null;
        }
        return tcpSipProviderMap.values().stream().findFirst().get();
    }

    public static SipProviderImpl getTcpSipProvider(String ip) {
        if (ObjectUtils.isEmpty(ip)) {
            return null;
        }
        return tcpSipProviderMap.get(ip);
    }

    public static void addListeningPoint(String monitorIp, int port) {
        monitorIpList.add(monitorIp);
        addListeningPoint(monitorIp, port, new SipProcessorObserver(), true);
    }

    public static void addListeningPoint(String monitorIp, int port, Boolean enableLog) {
        monitorIpList.add(monitorIp);
        addListeningPoint(monitorIp, port, new SipProcessorObserver(), enableLog);
    }

    public synchronized static void addListeningPoint(String monitorIp, int port, SipListener listener, Boolean enableLog) {
        SipStackImpl sipStack;
        try {
            Properties properties = DefaultProperties.getProperties("GB28181_SIP", monitorIp, enableLog);
            SipFactory sipFactory = SipFactory.getInstance();
            sipFactory.setPathName("gov.nist");
            sipStack = (SipStackImpl) sipFactory.createSipStack(properties);
            sipStack.setMessageParserFactory(new StringMsgParserFactory());
        } catch (PeerUnavailableException e) {
            log.error("[SIP SERVER] SIP服务启动失败， 监听地址{}失败,请检查ip是否正确", monitorIp, e);
            System.exit(0);
            return;
        }

        try {
            ListeningPoint tcpListeningPoint = sipStack.createListeningPoint(monitorIp, port, "TCP");
            SipProviderImpl tcpSipProvider = (SipProviderImpl) sipStack.createSipProvider(tcpListeningPoint);

            tcpSipProvider.setDialogErrorsAutomaticallyHandled();
            tcpSipProvider.addSipListener(listener);
            tcpSipProviderMap.put(monitorIp, tcpSipProvider);
            log.info("[SIP SERVER] tcp://{}:{} 启动成功", monitorIp, port);
        } catch (TransportNotSupportedException
                 | TooManyListenersException
                 | ObjectInUseException
                 | InvalidArgumentException e) {
            log.error("[SIP SERVER] tcp://{}:{} SIP服务启动失败,请检查端口是否被占用或者ip是否正确"
                    , monitorIp, port);
        }

        try {
            ListeningPoint udpListeningPoint = sipStack.createListeningPoint(monitorIp, port, "UDP");

            SipProviderImpl udpSipProvider = (SipProviderImpl) sipStack.createSipProvider(udpListeningPoint);
            udpSipProvider.addSipListener(listener);

            udpSipProviderMap.put(monitorIp, udpSipProvider);

            log.info("[SIP SERVER] udp://{}:{} 启动成功", monitorIp, port);
        } catch (TransportNotSupportedException
                 | TooManyListenersException
                 | ObjectInUseException
                 | InvalidArgumentException e) {
            log.error("[SIP SERVER] udp://{}:{} SIP服务启动失败,请检查端口是否被占用或者ip是否正确"
                    , monitorIp, port);
        }
    }

    public static String getMonitorIp() {
        return monitorIpList.get(0);
    }

    public String getLocalIp(String deviceLocalIp) {
        if (!ObjectUtils.isEmpty(deviceLocalIp)) {
            return deviceLocalIp;
        }
        return getUdpSipProvider().getListeningPoint().getIPAddress();
    }
}