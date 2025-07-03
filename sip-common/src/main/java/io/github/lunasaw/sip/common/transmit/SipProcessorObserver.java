package io.github.lunasaw.sip.common.transmit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson2.JSON;

import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import io.github.lunasaw.sip.common.transmit.event.timeout.ITimeoutProcessor;
import io.github.lunasaw.sip.common.async.AsyncSipMessageProcessor;
import io.github.lunasaw.sip.common.metrics.SipMetrics;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SIP信令处理类观察者
 *
 * @author luna
 */
@Slf4j
@Component
public class SipProcessorObserver implements SipListener {

    @Autowired
    private SipProcessorInject sipProcessorInject;
    
    @Autowired
    private AsyncSipMessageProcessor asyncMessageProcessor;
    
    @Autowired
    private SipMetrics sipMetrics;

    /**
     * 对SIP事件进行处理
     */
    private static final Map<String, List<SipRequestProcessor>> REQUEST_PROCESSOR_MAP  = new ConcurrentHashMap<>();
    /**
     * 处理接收IPCamera发来的SIP协议响应消息
     */
    private static final Map<String, SipResponseProcessor> RESPONSE_PROCESSOR_MAP = new ConcurrentHashMap<>();
    /**
     * 处理超时事件
     */
    private static final Map<String, ITimeoutProcessor> TIMEOUT_PROCESSOR_MAP = new ConcurrentHashMap<>();

    /**
     * 添加 request订阅
     *
     * @param method 方法名
     * @param processor 处理程序
     */
    public synchronized static void addRequestProcessor(String method, SipRequestProcessor processor) {
        if (REQUEST_PROCESSOR_MAP.containsKey(method)) {
            List<SipRequestProcessor> processors = REQUEST_PROCESSOR_MAP.get(method);
            processors.add(processor);
        } else {
            List<SipRequestProcessor> processors = new ArrayList<>();
            processors.add(processor);
            REQUEST_PROCESSOR_MAP.put(method, processors);
        }
    }

    /**
     * 添加 response订阅
     *
     * @param method 方法名
     * @param processor 处理程序
     */
    public synchronized static void addResponseProcessor(String method, SipResponseProcessor processor) {
        RESPONSE_PROCESSOR_MAP.put(method, processor);
    }

    /**
     * 添加 超时事件订阅
     *
     * @param processor 处理程序
     */
    public synchronized static void addTimeoutProcessor(String method, ITimeoutProcessor processor) {
        TIMEOUT_PROCESSOR_MAP.put(method, processor);
    }

    /**
     * 分发RequestEvent事件 - 优化版本，增加异步处理和性能监控
     *
     * @param requestEvent RequestEvent事件
     */
    @Override
    @Trace(operationName = "processRequest")
    public void processRequest(RequestEvent requestEvent) {
        Timer.Sample sample = sipMetrics.startTimer();
        String method = requestEvent.getRequest().getMethod();
        
        sipProcessorInject.before(requestEvent);

        try {
            // 记录方法调用
            sipMetrics.recordMethodCall(method);
            
            List<SipRequestProcessor> sipRequestProcessors = REQUEST_PROCESSOR_MAP.get(method);
            if (CollectionUtils.isEmpty(sipRequestProcessors)) {
                log.warn("暂不支持方法 {} 的请求", method);
                sipMetrics.recordError("UNSUPPORTED_METHOD", method);
                // TODO 回复错误码
                return;
            }

            // 异步处理请求
            asyncMessageProcessor.processRequestAsync(requestEvent).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("异步处理请求失败: method={}", method, throwable);
                    sipMetrics.recordError("ASYNC_PROCESSING_ERROR", method);
                } else {
                    sipMetrics.recordMessageProcessed(method, "SUCCESS");
                }
            });

            // 同步处理（保持兼容性）
            for (SipRequestProcessor sipRequestProcessor : sipRequestProcessors) {
                sipRequestProcessor.process(requestEvent);
            }
            
            sipMetrics.recordMessageProcessed();
            
        } catch (Exception e) {
            log.error("processRequest::requestEvent = {} ", requestEvent, e);
            sipMetrics.recordError("PROCESSING_ERROR", method);
            sipMetrics.recordMessageProcessed(method, "ERROR");
        } finally {
            sipMetrics.recordRequestProcessingTime(sample);
            sipProcessorInject.after();
        }
    }

    /**
     * 分发ResponseEvent事件 - 优化版本，增加异步处理和性能监控
     *
     * @param responseEvent responseEvent事件
     */
    @Override
    @Trace(operationName = "responseEvent")
    public void processResponse(ResponseEvent responseEvent) {
        Timer.Sample sample = sipMetrics.startTimer();
        Response response = responseEvent.getResponse();
        int status = response.getStatusCode();
        
        sipProcessorInject.before(responseEvent);

        try {
            // Success
            if (((status >= Response.OK) && (status < Response.MULTIPLE_CHOICES)) || status == Response.UNAUTHORIZED) {
                CSeqHeader cseqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
                String method = cseqHeader.getMethod();
                
                sipMetrics.recordMethodCall(method + "_RESPONSE");
                
                // 异步处理响应
                asyncMessageProcessor.processResponseAsync(responseEvent).whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("异步处理响应失败: method={}, status={}", method, status, throwable);
                        sipMetrics.recordError("ASYNC_RESPONSE_ERROR", method);
                    } else {
                        sipMetrics.recordMessageProcessed(method, "RESPONSE_SUCCESS");
                    }
                });
                
                SipResponseProcessor sipResponseProcessor = RESPONSE_PROCESSOR_MAP.get(method);
                if (sipResponseProcessor != null) {
                    sipResponseProcessor.process(responseEvent);
                }

                if (status != Response.UNAUTHORIZED && responseEvent.getResponse() != null && SipSubscribe.getOkSubscribesSize() > 0) {
                    SipSubscribe.publishOkEvent(responseEvent);
                }
                
                sipMetrics.recordMessageProcessed("RESPONSE", "SUCCESS");
                
            } else if ((status >= Response.TRYING) && (status < Response.OK)) {
                // 增加其它无需回复的响应，如101、180等
                sipMetrics.recordMessageProcessed("RESPONSE", "PROVISIONAL");
                
            } else {
                log.warn("接收到失败的response响应！status：" + status + ",message:" + response.getReasonPhrase() + " response = {}", responseEvent.getResponse());
                sipMetrics.recordError("FAILED_RESPONSE", String.valueOf(status));
                
                if (responseEvent.getResponse() != null && SipSubscribe.getErrorSubscribesSize() > 0) {
                    CallIdHeader callIdHeader = (CallIdHeader) responseEvent.getResponse().getHeader(CallIdHeader.NAME);
                    if (callIdHeader != null) {
                        Event subscribe = SipSubscribe.getErrorSubscribe(callIdHeader.getCallId());
                        if (subscribe != null) {
                            EventResult eventResult = new EventResult(responseEvent);
                            subscribe.response(eventResult);
                            SipSubscribe.removeErrorSubscribe(callIdHeader.getCallId());
                        }
                    }
                }
                if (responseEvent.getDialog() != null) {
                    responseEvent.getDialog().delete();
                }
                
                sipMetrics.recordMessageProcessed("RESPONSE", "ERROR");
            }
            
        } catch (Exception e) {
            log.error("processResponse error", e);
            sipMetrics.recordError("RESPONSE_PROCESSING_ERROR", String.valueOf(status));
        } finally {
            sipMetrics.recordResponseProcessingTime(sample);
            sipProcessorInject.after();
        }
    }

    /**
     * 向超时订阅发送消息
     *
     * @param timeoutEvent timeoutEvent事件
     */
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        ClientTransaction clientTransaction = timeoutEvent.getClientTransaction();

        if (clientTransaction == null) {
            return;
        }

        Request request = clientTransaction.getRequest();
        if (request == null) {
            return;
        }

        CallIdHeader callIdHeader = (CallIdHeader)request.getHeader(CallIdHeader.NAME);
        if (callIdHeader != null) {
            Event subscribe = SipSubscribe.getErrorSubscribe(callIdHeader.getCallId());
            EventResult eventResult = new EventResult(timeoutEvent);
            if (subscribe != null) {
                subscribe.response(eventResult);
            }
            SipSubscribe.removeOkSubscribe(callIdHeader.getCallId());
            SipSubscribe.removeErrorSubscribe(callIdHeader.getCallId());
        }
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        log.error("processIOException::exceptionEvent = {} ", JSON.toJSONString(exceptionEvent));
    }

    /**
     * 事物结束
     *
     * @param timeoutEvent -- an event that indicates that the
     * transaction has transitioned into the terminated state.
     */
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent timeoutEvent) {
        EventResult eventResult = new EventResult(timeoutEvent);

        Event timeOutSubscribe = SipSubscribe.getErrorSubscribe(eventResult.getCallId());
        if (timeOutSubscribe != null) {
            timeOutSubscribe.response(eventResult);
        }
    }

    /**
     * 会话结束
     *
     * @param dialogTerminatedEvent -- an event that indicates that the
     * dialog has transitioned into the terminated state.
     */
    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        EventResult eventResult = new EventResult(dialogTerminatedEvent);

        Event timeOutSubscribe = SipSubscribe.getErrorSubscribe(eventResult.getCallId());
        if (timeOutSubscribe != null) {
            timeOutSubscribe.response(eventResult);
        }
    }
}
