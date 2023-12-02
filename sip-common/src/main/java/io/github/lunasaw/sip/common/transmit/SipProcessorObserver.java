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
import org.springframework.scheduling.annotation.Async;

import com.luna.common.thread.AsyncEngineUtils;

import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.sip.common.transmit.event.EventPublisher;
import io.github.lunasaw.sip.common.transmit.event.EventResult;
import io.github.lunasaw.sip.common.transmit.event.SipSubscribe;
import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;
import io.github.lunasaw.sip.common.transmit.event.timeout.ITimeoutProcessor;
import lombok.extern.slf4j.Slf4j;

/**
 * SIP信令处理类观察者
 *
 * @author luna
 */
@Slf4j
public class SipProcessorObserver implements SipListener {

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

    private static final List<EventPublisher> eventPublishers = new ArrayList<>();

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
     *
     * @param requestEvent RequestEvent事件
     */
    @Override
    public void processRequest(RequestEvent requestEvent) {
        AsyncEngineUtils.execute(() -> {
            String method = requestEvent.getRequest().getMethod();
            List<SipRequestProcessor> sipRequestProcessors = REQUEST_PROCESSOR_MAP.get(method);
            if (CollectionUtils.isEmpty(sipRequestProcessors)) {
                log.warn("暂不支持方法 {} 的请求", method);
                // TODO 回复错误玛
                return;
            }
            try {
                for (SipRequestProcessor sipRequestProcessor : sipRequestProcessors) {
                    sipRequestProcessor.process(requestEvent);
                }
            } catch (Exception e) {
                log.error("processRequest::requestEvent = {} ", requestEvent, e);
            }
        });
    }

    /**
     * 分发ResponseEvent事件
     *
     * @param responseEvent responseEvent事件
     */
    @Override
    @Async("taskExecutor")
    public void processResponse(ResponseEvent responseEvent) {
        Response response = responseEvent.getResponse();
        int status = response.getStatusCode();

        // Success
        if (((status >= Response.OK) && (status < Response.MULTIPLE_CHOICES)) || status == Response.UNAUTHORIZED) {
            CSeqHeader cseqHeader = (CSeqHeader) responseEvent.getResponse().getHeader(CSeqHeader.NAME);
            String method = cseqHeader.getMethod();
            SipResponseProcessor sipResponseProcessor = RESPONSE_PROCESSOR_MAP.get(method);
            if (sipResponseProcessor != null) {
                sipResponseProcessor.process(responseEvent);
            }

            if (status != Response.UNAUTHORIZED && responseEvent.getResponse() != null && SipSubscribe.getOkSubscribesSize() > 0) {
                SipSubscribe.publishOkEvent(responseEvent);
            }
        } else if ((status >= Response.TRYING) && (status < Response.OK)) {
            // 增加其它无需回复的响应，如101、180等
        } else {
            log.warn("接收到失败的response响应！status：" + status + ",message:" + response.getReasonPhrase() + " response = {}", responseEvent.getResponse());
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
        }

    }

    /**
     * 向超时订阅发送消息
     *
     * @param timeoutEvent timeoutEvent事件
     */
    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        log.info("[消息发送超时]");
        ClientTransaction clientTransaction = timeoutEvent.getClientTransaction();

        if (clientTransaction != null) {
            log.info("[发送错误订阅] clientTransaction != null");
            Request request = clientTransaction.getRequest();
            if (request != null) {
                log.info("[发送错误订阅] request != null");
                CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
                if (callIdHeader != null) {
                    log.info("[发送错误订阅]");
                    Event subscribe = SipSubscribe.getErrorSubscribe(callIdHeader.getCallId());
                    EventResult eventResult = new EventResult(timeoutEvent);
                    if (subscribe != null) {
                        subscribe.response(eventResult);
                    }
                    SipSubscribe.removeOkSubscribe(callIdHeader.getCallId());
                    SipSubscribe.removeErrorSubscribe(callIdHeader.getCallId());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(eventPublishers)) {
            for (EventPublisher eventPublisher : eventPublishers) {
                eventPublisher.requestTimeOut(timeoutEvent);
            }
        }
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        System.out.println("processIOException");
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // if (transactionTerminatedEvent.isServerTransaction()) {
        // ServerTransaction serverTransaction = transactionTerminatedEvent.getServerTransaction();
        // serverTransaction.get
        // }

        // Transaction transaction = null;
        // System.out.println("processTransactionTerminated");
        // if (transactionTerminatedEvent.isServerTransaction()) {
        // transaction = transactionTerminatedEvent.getServerTransaction();
        // }else {
        // transaction = transactionTerminatedEvent.getClientTransaction();
        // }
        //
        // System.out.println(transaction.getBranchId());
        // System.out.println(transaction.getState());
        // System.out.println(transaction.getRequest().getMethod());
        // CallIdHeader header = (CallIdHeader)transaction.getRequest().getHeader(CallIdHeader.NAME);
        // SipSubscribe.EventResult<TransactionTerminatedEvent> terminatedEventEventResult = new
        // SipSubscribe.EventResult<>(transactionTerminatedEvent);

        // SipSubscribe.getErrorSubscribe(header.getCallId()).response(terminatedEventEventResult);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        CallIdHeader callId = dialogTerminatedEvent.getDialog().getCallId();
    }

    public void addPusher(EventPublisher eventPublisher) {
        eventPublishers.add(eventPublisher);
    }
}
