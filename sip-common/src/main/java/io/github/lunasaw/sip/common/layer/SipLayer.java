package io.github.lunasaw.sip.common.layer;

import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.*;

import io.github.lunasaw.sip.common.conf.DefaultProperties;
import io.github.lunasaw.sip.common.transmit.SipProcessorObserver;
import io.github.lunasaw.sip.common.conf.msg.StringMsgParserFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.util.ObjectUtils;

import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.SipStackImpl;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 */
@Slf4j
public class SipLayer implements CommandLineRunner {

    private static final Map<String, SipProviderImpl> tcpSipProviderMap = new ConcurrentHashMap<>();
    private static final Map<String, SipProviderImpl> udpSipProviderMap = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {

    }

    public static SipProviderImpl getUdpSipProvider(String ip) {
		if (ObjectUtils.isEmpty(ip)) {
			return null;
		}
		return udpSipProviderMap.get(ip);
	}

    public static SipProviderImpl getUdpSipProvider() {
        if (udpSipProviderMap.size() < 1) {
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
		addListeningPoint(monitorIp, port, new SipProcessorObserver(), true);
    }

    public static void addListeningPoint(String monitorIp, int port, Boolean enableLog) {
		addListeningPoint(monitorIp, port, new SipProcessorObserver(), enableLog);
	}

	public synchronized static void addListeningPoint(String monitorIp, int port, SipListener listener, Boolean enableLog) {
		SipStackImpl sipStack;
		try {
			sipStack = (SipStackImpl) SipFactory.getInstance().createSipStack(DefaultProperties.getProperties("GB28181_SIP", enableLog));
			sipStack.setMessageParserFactory(new StringMsgParserFactory());
		} catch (PeerUnavailableException e) {
			log.error("[SIP SERVER] SIP服务启动失败， 监听地址{}失败,请检查ip是否正确", monitorIp);
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

	public String getLocalIp(String deviceLocalIp) {
		if (!ObjectUtils.isEmpty(deviceLocalIp)) {
			return deviceLocalIp;
		}
		return getUdpSipProvider().getListeningPoint().getIPAddress();
	}
}