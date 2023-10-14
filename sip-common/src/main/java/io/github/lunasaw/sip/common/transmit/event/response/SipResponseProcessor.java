package io.github.lunasaw.sip.common.transmit.event.response;

import javax.sip.ResponseEvent;

/**    
 * 处理接收IPCamera发来的SIP协议响应消息
 * @author swwheihei
 */
public interface SipResponseProcessor {

	/**
	 * 处理接收IPCamera发来的SIP协议响应消息
	 * @param evt 消息对象
	 */
	void process(ResponseEvent evt);


}
