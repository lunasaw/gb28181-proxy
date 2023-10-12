package io.github.lunasaw.gbproxy.common.layer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import gov.nist.javax.sip.SipProviderImpl;
import io.github.lunasaw.gbproxy.common.transmit.ISipProcessorObserver;
import org.springframework.util.ObjectUtils;

@Component
@Order(value = 10)
public class SipLayer implements CommandLineRunner {

    private final Map<String, SipProviderImpl> tcpSipProviderMap = new ConcurrentHashMap<>();
    private final Map<String, SipProviderImpl> udpSipProviderMap = new ConcurrentHashMap<>();
    @Autowired
    private ISipProcessorObserver              sipProcessorObserver;

    @Override
    public void run(String... args) throws Exception {

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