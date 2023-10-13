package io.github.lunasaw.gbproxy.common.layer;

import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.ConcurrentHashMap;

import gov.nist.javax.sip.SipStackImpl;
import io.github.lunasaw.gbproxy.common.conf.DefaultProperties;
import io.github.lunasaw.gbproxy.common.conf.msg.GbStringMsgParserFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.SipProviderImpl;
import io.github.lunasaw.gbproxy.common.transmit.ISipProcessorObserver;
import org.springframework.util.ObjectUtils;

import javax.sip.*;

/**
 * @author luna
 */
@Component
@Order(value = 10)
@Slf4j
public class SipLayer implements CommandLineRunner {

    private final Map<String, SipProviderImpl> tcpSipProviderMap = new ConcurrentHashMap<>();
    private final Map<String, SipProviderImpl> udpSipProviderMap = new ConcurrentHashMap<>();
    @Autowired
    private ISipProcessorObserver              sipProcessorObserver;

    @Override
    public void run(String... args) throws Exception {

    }

	@Value("${sip.log:false}")
	private Boolean enableLog;


	public void addListeningPoint(String monitorIp, int port) {
		SipStackImpl sipStack;
		try {
			sipStack = (SipStackImpl) SipFactory.getInstance().createSipStack(DefaultProperties.getProperties("GB28181_SIP", enableLog));
			sipStack.setMessageParserFactory(new GbStringMsgParserFactory());
		} catch (PeerUnavailableException e) {
			log.error("[SIP SERVER] SIP服务启动失败， 监听地址{}失败,请检查ip是否正确", monitorIp);
			return;
		}

		try {
			ListeningPoint tcpListeningPoint = sipStack.createListeningPoint(monitorIp, port, "TCP");
			SipProviderImpl tcpSipProvider = (SipProviderImpl) sipStack.createSipProvider(tcpListeningPoint);

			tcpSipProvider.setDialogErrorsAutomaticallyHandled();
			tcpSipProvider.addSipListener(sipProcessorObserver);
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
			udpSipProvider.addSipListener(sipProcessorObserver);

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

	public SipProviderImpl getUdpSipProvider(String ip) {
		if (ObjectUtils.isEmpty(ip)) {
			return null;
		}
		return udpSipProviderMap.get(ip);
	}

	public SipProviderImpl getUdpSipProvider() {
		if (udpSipProviderMap.size() != 1) {
			return null;
		}
		return udpSipProviderMap.values().stream().findFirst().get();
	}

	public SipProviderImpl getTcpSipProvider() {
		if (tcpSipProviderMap.size() != 1) {
			return null;
		}
		return tcpSipProviderMap.values().stream().findFirst().get();
	}

	public SipProviderImpl getTcpSipProvider(String ip) {
		if (ObjectUtils.isEmpty(ip)) {
			return null;
		}
		return tcpSipProviderMap.get(ip);
	}

	public String getLocalIp(String deviceLocalIp) {
		if (!ObjectUtils.isEmpty(deviceLocalIp)) {
			return deviceLocalIp;
		}
		return getUdpSipProvider().getListeningPoint().getIPAddress();
	}
}