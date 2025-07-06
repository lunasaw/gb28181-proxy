package io.github.lunasaw.sip.common.transmit;

import com.alibaba.fastjson2.JSON;
import io.github.lunasaw.sip.common.metrics.SipMetrics;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import io.github.lunasaw.sip.common.transmit.event.timeout.ITimeoutProcessor;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.skywalking.apm.toolkit.trace.Trace;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SIP监听器抽象基类
 * 提供基础统一的SIP事件处理能力，支持自定义Processor的添加
 *
 * @author luna
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractSipListener implements SipListener {


    /**
     * 对SIP事件进行处理
     */
    protected final Map<String, List<SipRequestProcessor>> REQUEST_PROCESSOR_MAP = new ConcurrentHashMap<>();
    ;
    /**
     * 处理接收IPCamera发来的SIP协议响应消息
     */
    protected final Map<String, SipResponseProcessor> RESPONSE_PROCESSOR_MAP = new ConcurrentHashMap<>();
    /**
     * 处理超时事件
     */
    protected final Map<String, ITimeoutProcessor> TIMEOUT_PROCESSOR_MAP = new ConcurrentHashMap<>();

    /**
     * SIP指标收集器
     */
    @Getter
    @Setter
    protected SipMetrics sipMetrics = new SipMetrics(new SimpleMeterRegistry());


    /**
     * 添加 request订阅
     *
     * @param method    方法名
     * @param processor 处理程序
     */
    public synchronized void addRequestProcessor(String method, SipRequestProcessor processor) {
        if (REQUEST_PROCESSOR_MAP.containsKey(method)) {
            List<SipRequestProcessor> processors = REQUEST_PROCESSOR_MAP.get(method);
            processors.add(processor);
        } else {
            List<SipRequestProcessor> processors = new ArrayList<>();
            processors.add(processor);
            REQUEST_PROCESSOR_MAP.put(method, processors);
        }
        log.debug("添加请求处理器: {} -> {}", method, processor.getClass().getSimpleName());
    }

    /**
     * 添加 response订阅
     *
     * @param method    方法名
     * @param processor 处理程序
     */
    public synchronized void addResponseProcessor(String method, SipResponseProcessor processor) {
        RESPONSE_PROCESSOR_MAP.put(method, processor);
        log.debug("添加响应处理器: {} -> {}", method, processor.getClass().getSimpleName());
    }

    /**
     * 添加 超时事件订阅
     *
     * @param method    方法名
     * @param processor 处理程序
     */
    public synchronized void addTimeoutProcessor(String method, ITimeoutProcessor processor) {
        TIMEOUT_PROCESSOR_MAP.put(method, processor);
        log.debug("添加超时处理器: {} -> {}", method, processor.getClass().getSimpleName());
    }

    /**
     * 移除请求处理器
     *
     * @param method    方法名
     * @param processor 处理程序
     */
    public synchronized void removeRequestProcessor(String method, SipRequestProcessor processor) {
        List<SipRequestProcessor> processors = REQUEST_PROCESSOR_MAP.get(method);
        if (processors != null) {
            processors.remove(processor);
            if (processors.isEmpty()) {
                REQUEST_PROCESSOR_MAP.remove(method);
            }
            log.debug("移除请求处理器: {} -> {}", method, processor.getClass().getSimpleName());
        }
    }

    /**
     * 移除响应处理器
     *
     * @param method 方法名
     */
    public synchronized void removeResponseProcessor(String method) {
        RESPONSE_PROCESSOR_MAP.remove(method);
        log.debug("移除响应处理器: {}", method);
    }

    /**
     * 移除超时处理器
     *
     * @param method 方法名
     */
    public synchronized void removeTimeoutProcessor(String method) {
        TIMEOUT_PROCESSOR_MAP.remove(method);
        log.debug("移除超时处理器: {}", method);
    }

    /**
     * 获取请求处理器列表
     *
     * @param method 方法名
     * @return 处理器列表
     */
    public List<SipRequestProcessor> getRequestProcessors(String method) {
        return REQUEST_PROCESSOR_MAP.get(method);
    }

    /**
     * 获取响应处理器
     *
     * @param method 方法名
     * @return 处理器
     */
    public SipResponseProcessor getResponseProcessor(String method) {
        return RESPONSE_PROCESSOR_MAP.get(method);
    }

    /**
     * 获取超时处理器
     *
     * @param method 方法名
     * @return 处理器
     */
    public ITimeoutProcessor getTimeoutProcessor(String method) {
        return TIMEOUT_PROCESSOR_MAP.get(method);
    }

    /**
     * 分发RequestEvent事件 - 基础实现
     *
     * @param requestEvent RequestEvent事件
     */
    @Override
    public void processRequest(RequestEvent requestEvent) {
        Timer.Sample sample = sipMetrics != null ? sipMetrics.startTimer() : null;
        String method = requestEvent.getRequest().getMethod();

        try {
            // 记录方法调用
            if (sipMetrics != null) {
                sipMetrics.recordMethodCall(method);
            }

            List<SipRequestProcessor> sipRequestProcessors = REQUEST_PROCESSOR_MAP.get(method);
            if (CollectionUtils.isEmpty(sipRequestProcessors)) {
                log.warn("暂不支持方法 {} 的请求", method);
                if (sipMetrics != null) {
                    sipMetrics.recordError("UNSUPPORTED_METHOD", method);
                }
                // 调用子类自定义处理
                handleUnsupportedRequest(requestEvent);
                return;
            }

            // 同步处理请求
            for (SipRequestProcessor sipRequestProcessor : sipRequestProcessors) {
                sipRequestProcessor.process(requestEvent);
            }

            if (sipMetrics != null) {
                sipMetrics.recordMessageProcessed();
            }

        } catch (Exception e) {
            log.error("processRequest::requestEvent = {} ", requestEvent, e);
            if (sipMetrics != null) {
                sipMetrics.recordError("PROCESSING_ERROR", method);
                sipMetrics.recordMessageProcessed(method, "ERROR");
            }
            // 调用子类异常处理
            handleRequestException(requestEvent, e);
        } finally {
            if (sipMetrics != null && sample != null) {
                sipMetrics.recordRequestProcessingTime(sample);
            }
        }
    }

    /**
     * 分发ResponseEvent事件 - 基础实现
     *
     * @param responseEvent responseEvent事件
     */
    @Override
    public void processResponse(ResponseEvent responseEvent) {
        Timer.Sample sample = sipMetrics != null ? sipMetrics.startTimer() : null;
        Response response = responseEvent.getResponse();
        int status = response.getStatusCode();
        try {
            // Success
            if (((status >= Response.OK) && (status < Response.MULTIPLE_CHOICES)) || status == Response.UNAUTHORIZED) {
                CSeqHeader cseqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
                String method = cseqHeader.getMethod();

                if (sipMetrics != null) {
                    sipMetrics.recordMethodCall(method + "_RESPONSE");
                }

                SipResponseProcessor sipResponseProcessor = RESPONSE_PROCESSOR_MAP.get(method);
                if (sipResponseProcessor != null) {
                    sipResponseProcessor.process(responseEvent);
                }

                if (status != Response.UNAUTHORIZED && responseEvent.getResponse() != null && SipSubscribe.getOkSubscribesSize() > 0) {
                    SipSubscribe.publishOkEvent(responseEvent);
                }

                if (sipMetrics != null) {
                    sipMetrics.recordMessageProcessed("RESPONSE", "SUCCESS");
                }

            } else if ((status >= Response.TRYING) && (status < Response.OK)) {
                // 增加其它无需回复的响应，如101、180等
                if (sipMetrics != null) {
                    sipMetrics.recordMessageProcessed("RESPONSE", "PROVISIONAL");
                }

            } else {
                log.warn("接收到失败的response响应！status：" + status + ",message:" + response.getReasonPhrase() + " response = {}",
                        responseEvent.getResponse());
                if (sipMetrics != null) {
                    sipMetrics.recordError("FAILED_RESPONSE", String.valueOf(status));
                }

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

                if (sipMetrics != null) {
                    sipMetrics.recordMessageProcessed("RESPONSE", "ERROR");
                }
            }

        } catch (Exception e) {
            log.error("processResponse error", e);
            if (sipMetrics != null) {
                sipMetrics.recordError("RESPONSE_PROCESSING_ERROR", String.valueOf(status));
            }
            // 调用子类异常处理
            handleResponseException(responseEvent, e);
        } finally {
            if (sipMetrics != null && sample != null) {
                sipMetrics.recordResponseProcessingTime(sample);
            }
        }
    }

    /**
     * 向超时订阅发送消息 - 基础实现
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

        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
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
        // 调用子类异常处理
        handleIOException(exceptionEvent);
    }

    /**
     * 事物结束 - 基础实现
     *
     * @param timeoutEvent -- an event that indicates that the
     *                     transaction has transitioned into the terminated state.
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
     * 会话结束 - 基础实现
     *
     * @param dialogTerminatedEvent -- an event that indicates that the
     *                              dialog has transitioned into the terminated state.
     */
    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        EventResult eventResult = new EventResult(dialogTerminatedEvent);

        Event timeOutSubscribe = SipSubscribe.getErrorSubscribe(eventResult.getCallId());
        if (timeOutSubscribe != null) {
            timeOutSubscribe.response(eventResult);
        }
    }

    // ==================== 子类可重写的方法 ====================

    /**
     * 处理不支持的请求（子类可重写）
     *
     * @param requestEvent 请求事件
     */
    protected void handleUnsupportedRequest(RequestEvent requestEvent) {
        // 默认实现为空，子类可重写
    }

    /**
     * 处理请求异常（子类可重写）
     *
     * @param requestEvent 请求事件
     * @param exception    异常
     */
    protected void handleRequestException(RequestEvent requestEvent, Exception exception) {
        // 默认实现为空，子类可重写
    }

    /**
     * 处理响应异常（子类可重写）
     *
     * @param responseEvent 响应事件
     * @param exception     异常
     */
    protected void handleResponseException(ResponseEvent responseEvent, Exception exception) {
        // 默认实现为空，子类可重写
    }

    /**
     * 处理IO异常（子类可重写）
     *
     * @param exceptionEvent IO异常事件
     */
    protected void handleIOException(IOExceptionEvent exceptionEvent) {
        // 默认实现为空，子类可重写
    }

    /**
     * 获取处理器统计信息
     *
     * @return 统计信息
     */
    public String getProcessorStats() {
        return String.format("RequestProcessors: %d, ResponseProcessors: %d, TimeoutProcessors: %d",
                REQUEST_PROCESSOR_MAP.size(), RESPONSE_PROCESSOR_MAP.size(), TIMEOUT_PROCESSOR_MAP.size());
    }
}