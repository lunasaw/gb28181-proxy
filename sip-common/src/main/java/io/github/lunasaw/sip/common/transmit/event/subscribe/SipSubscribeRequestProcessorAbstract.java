// package io.github.lunasaw.sip.common.transmit.event.subscribe;
//
// import java.nio.charset.Charset;
// import java.util.Map;
// import java.util.Optional;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.ConcurrentMap;
//
// import javax.sip.RequestEvent;
//
// import org.apache.commons.collections4.MapUtils;
//
// import com.google.common.collect.Maps;
// import com.luna.common.text.StringTools;
//
// import gov.nist.javax.sip.message.SIPRequest;
// import io.github.lunasaw.sip.common.constant.Constant;
// import io.github.lunasaw.sip.common.entity.Device;
// import io.github.lunasaw.sip.common.entity.FromDevice;
// import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessorAbstract;
// import io.github.lunasaw.sip.common.utils.XmlUtils;
// import lombok.extern.slf4j.Slf4j;
//
/// **
// * @author luna
// */
// @Slf4j
// public abstract class SipSubscribeRequestProcessorAbstract extends SipRequestProcessorAbstract {
//
// public static final Map<String, Map<String, SubscribeHandler>> SUBSCRIBE_HANDLER_CMD_MAP = new ConcurrentHashMap<>();
//
// public static void addHandler(SubscribeHandler SubscribeHandler) {
// if (SubscribeHandler == null) {
// return;
// }
//
// if (SUBSCRIBE_HANDLER_CMD_MAP.containsKey(SubscribeHandler.getRootType())) {
// SUBSCRIBE_HANDLER_CMD_MAP.get(SubscribeHandler.getRootType()).put(SubscribeHandler.getCmdType(), SubscribeHandler);
// } else {
// ConcurrentMap<String, SubscribeHandler> newedConcurrentMap = Maps.newConcurrentMap();
// newedConcurrentMap.put(SubscribeHandler.getCmdType(), SubscribeHandler);
// SUBSCRIBE_HANDLER_CMD_MAP.put(SubscribeHandler.getRootType(), newedConcurrentMap);
// }
// }
//
// public void doSubscribeHandForEvt(RequestEvent evt, FromDevice fromDevice) {
// SIPRequest request = (SIPRequest)evt.getRequest();
//
// String charset = Optional.of(fromDevice).map(Device::getCharset).orElse(Constant.UTF_8);
//
// // 解析xml
// byte[] rawContent = request.getRawContent();
// String xmlStr = StringTools.toEncodedString(rawContent, Charset.forName(charset));
//
// String cmdType = XmlUtils.getCmdType(xmlStr);
// String rootType = XmlUtils.getRootType(xmlStr);
//
// Map<String, SubscribeHandler> SubscribeHandlerMap = SUBSCRIBE_HANDLER_CMD_MAP.get(rootType);
//
// if (MapUtils.isEmpty(SubscribeHandlerMap)) {
// return;
// }
//
// SubscribeHandler SubscribeHandler = SubscribeHandlerMap.get(cmdType);
// if (SubscribeHandler == null) {
// return;
// }
// try {
// SubscribeHandler.setXmlStr(xmlStr);
// SubscribeHandler.handForEvt(evt);
// SubscribeHandler.responseAck(evt);
// } catch (Exception e) {
// log.error("process::evt = {}, e = {}", evt, e.getMessage());
// SubscribeHandler.responseError(evt);
// }
// }
//
// }
