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
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;

import io.github.lunasaw.sip.common.async.SipAsyncManager;
import io.github.lunasaw.sip.common.exception.SipExceptionHandler;
import io.github.lunasaw.sip.common.exception.SipProcessorException;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import io.github.lunasaw.sip.common.transmit.event.timeout.ITimeoutProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP信令处理类观察者
 * 集成异步处理管理器，支持高并发消息处理
 *
 * @author luna
 */
@Slf4j
@Component
public class SipProcessorObserver implements SipListener {

    @Autowired
    private SipProcessorInject sipProcessorInject;

    @Autowired
    private SipExceptionHandler                                 sipExceptionHandler;

    @Autowired
    private SipAsyncManager                                     sipAsyncManager;

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
     * 分发RequestEvent事件
     * 集成异步处理管理器，根据配置和处理器特性选择执行策略
     *
     * @param requestEvent RequestEvent事件
     */
    @Override
    @Trace(operationName = "processRequest")
    public void processRequest(RequestEvent requestEvent) {
        sipProcessorInject.before(requestEvent);

        String method = requestEvent.getRequest().getMethod();
        List<SipRequestProcessor> sipRequestProcessors = REQUEST_PROCESSOR_MAP.get(method);

        if (CollectionUtils.isEmpty(sipRequestProcessors)) {
            log.warn("暂不支持方法 {} 的请求", method);
            sipExceptionHandler.handleException(
                new SipProcessorException("UNKNOWN", method, "不支持的SIP方法: " + method),
                requestEvent);
            sipProcessorInject.after();
            return;
        }

        // 使用异步管理器处理请求
        sipAsyncManager.processRequest(sipRequestProcessors, requestEvent)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("SIP请求处理完成但有异常: {}", method, throwable);
                } else {
                    log.debug("SIP请求处理完成: {}", method);
                }
                sipProcessorInject.after();
            });
    }

    /**
     * 分发ResponseEvent事件
     *
     * @param responseEvent responseEvent事件
     */
    @Override
    @Trace(operationName = "processResponse")
    public void processResponse(ResponseEvent responseEvent) {
        sipProcessorInject.before(responseEvent);

        try {
            Response response = responseEvent.getResponse();
            int status = response.getStatusCode();

            // Success
            if (((status >= Response.OK) && (status < Response.MULTIPLE_CHOICES)) || status == Response.UNAUTHORIZED) {
                handleSuccessResponse(responseEvent, status);
            } else if ((status >= Response.TRYING) && (status < Response.OK)) {
                // 增加其它无需回复的响应，如101、180等
                log.debug("收到临时响应: {}", status);
            } else {
                handleErrorResponse(responseEvent, status);
            }
        } catch (Exception e) {
            log.error("处理SIP响应时发生异常", e);
            // 对于响应处理异常，我们只记录日志，不再发送响应
        }

        sipProcessorInject.after();
    }

    /**
     * 处理成功响应
     */
    private void handleSuccessResponse(ResponseEvent responseEvent, int status) {
        CSeqHeader cseqHeader = (CSeqHeader)responseEvent.getResponse().getHeader(CSeqHeader.NAME);
        String method = cseqHeader.getMethod();
        SipResponseProcessor sipResponseProcessor = RESPONSE_PROCESSOR_MAP.get(method);

        if (sipResponseProcessor != null) {
            // 使用异步管理器处理响应
            sipAsyncManager.processResponse(sipResponseProcessor, responseEvent)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        String processorName = sipResponseProcessor.getClass().getSimpleName();
                        log.error("SIP响应处理器异常: {} 方法: {} 状态码: {}", processorName, method, status, throwable);
                    } else {
                        log.debug("成功处理SIP响应: {} 状态码: {}", method, status);
                    }
                });
        } else {
            log.debug("未找到对应的响应处理器: {}", method);
        }

        if (status != Response.UNAUTHORIZED && responseEvent.getResponse() != null && SipSubscribe.getOkSubscribesSize() > 0) {
            SipSubscribe.publishOkEvent(responseEvent);
        }
    }

    /**
     * 处理错误响应
     */
    private void handleErrorResponse(ResponseEvent responseEvent, int status) {
        Response response = responseEvent.getResponse();
        log.warn("接收到失败的response响应！status：{}, message: {}", status, response.getReasonPhrase());

        if (responseEvent.getResponse() != null && SipSubscribe.getErrorSubscribesSize() > 0) {
            CallIdHeader callIdHeader = (CallIdHeader)responseEvent.getResponse().getHeader(CallIdHeader.NAME);
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
    }

    /**
     * 向超时订阅发送消息
     *
     * @param timeoutEvent timeoutEvent事件
     */
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        try {
            ClientTransaction clientTransaction = timeoutEvent.getClientTransaction();

            if (clientTransaction == null) {
                log.debug("收到超时事件但客户端事务为空");
                return;
            }

            Request request = clientTransaction.getRequest();
            if (request == null) {
                log.debug("收到超时事件但请求为空");
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

                log.warn("SIP请求超时: {} CallID: {}", request.getMethod(), callIdHeader.getCallId());
            }
        } catch (Exception e) {
            log.error("处理SIP超时事件异常", e);
        }
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        log.error("SIP IO异常: {}", JSON.toJSONString(exceptionEvent));
    }

    /**
     * 事物结束
     *
     * @param timeoutEvent -- an event that indicates that the
     * transaction has transitioned into the terminated state.
     */
    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent timeoutEvent) {
        try {
            EventResult eventResult = new EventResult(timeoutEvent);

            Event timeOutSubscribe = SipSubscribe.getErrorSubscribe(eventResult.getCallId());
            if (timeOutSubscribe != null) {
                timeOutSubscribe.response(eventResult);
            }

            log.debug("SIP事务终止: CallID: {}", eventResult.getCallId());
        } catch (Exception e) {
            log.error("处理SIP事务终止事件异常", e);
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
        try {
            EventResult eventResult = new EventResult(dialogTerminatedEvent);

            Event timeOutSubscribe = SipSubscribe.getErrorSubscribe(eventResult.getCallId());
            if (timeOutSubscribe != null) {
                timeOutSubscribe.response(eventResult);
            }

            log.debug("SIP对话终止: CallID: {}", eventResult.getCallId());
        } catch (Exception e) {
            log.error("处理SIP对话终止事件异常", e);
        }
    }

    /**
     * 获取异步处理状态信息
     */
    public String getAsyncProcessorStatus() {
        return sipAsyncManager.getAsyncExecutorStatus();
    }
}
