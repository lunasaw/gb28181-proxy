package io.github.lunasaw.gbproxy.common.transmit.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

import io.github.lunasaw.gbproxy.common.transmit.ISipProcessorObserver;
import io.github.lunasaw.gbproxy.common.transmit.event.Event;
import io.github.lunasaw.gbproxy.common.transmit.event.EventResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.common.transmit.event.EventPublisher;
import io.github.lunasaw.gbproxy.common.transmit.event.SipSubscribe;
import io.github.lunasaw.gbproxy.common.transmit.event.request.ISipRequestProcessor;
import io.github.lunasaw.gbproxy.common.transmit.event.response.ISipResponseProcessor;
import io.github.lunasaw.gbproxy.common.transmit.event.timeout.ITimeoutProcessor;

/**
 * SIP信令处理类观察者
 * @author luna
 */
@Component
public class SipProcessorObserverImpl implements ISipProcessorObserver {

    private final static Logger                             logger               = LoggerFactory.getLogger(SipProcessorObserverImpl.class);

    /**
     * 对SIP事件进行处理
     */
    private static final Map<String, ISipRequestProcessor> REQUEST_PROCESSOR_MAP = new ConcurrentHashMap<>();
    /**
     * 处理接收IPCamera发来的SIP协议响应消息
     */
    private static final Map<String, ISipResponseProcessor> RESPONSE_PROCESSOR_MAP = new ConcurrentHashMap<>();
    /**
     * 处理超时事件
     */
    private static ITimeoutProcessor                        timeoutProcessor;

    @Autowired
    private SipSubscribe                                    sipSubscribe;

    @Autowired
    private EventPublisher                                  eventPublisher;

    /**
     * 添加 request订阅
     * 
     * @param method 方法名
     * @param processor 处理程序
     */
    public void addRequestProcessor(String method, ISipRequestProcessor processor) {
        REQUEST_PROCESSOR_MAP.put(method, processor);
    }

    /**
     * 添加 response订阅
     * 
     * @param method 方法名
     * @param processor 处理程序
     */
    public void addResponseProcessor(String method, ISipResponseProcessor processor) {
        RESPONSE_PROCESSOR_MAP.put(method, processor);
    }

    /**
     * 添加 超时事件订阅
     * 
     * @param processor 处理程序
     */
    public void addTimeoutProcessor(ITimeoutProcessor processor) {
        timeoutProcessor = processor;
    }

    /**
     * 分发RequestEvent事件
     * 
     * @param requestEvent RequestEvent事件
     */
    @Override
    @Async("taskExecutor")
    public void processRequest(RequestEvent requestEvent) {
        String method = requestEvent.getRequest().getMethod();
        ISipRequestProcessor sipRequestProcessor = REQUEST_PROCESSOR_MAP.get(method);
        if (sipRequestProcessor == null) {
            logger.warn("不支持方法{}的request", method);
            // TODO 回复错误玛
            return;
        }
        REQUEST_PROCESSOR_MAP.get(method).process(requestEvent);

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
            CSeqHeader cseqHeader = (CSeqHeader)responseEvent.getResponse().getHeader(CSeqHeader.NAME);
            String method = cseqHeader.getMethod();
            ISipResponseProcessor sipRequestProcessor = RESPONSE_PROCESSOR_MAP.get(method);
            if (sipRequestProcessor != null) {
                sipRequestProcessor.process(responseEvent);
            }
            if (status != Response.UNAUTHORIZED && responseEvent.getResponse() != null && sipSubscribe.getOkSubscribesSize() > 0) {
                CallIdHeader callIdHeader = (CallIdHeader)responseEvent.getResponse().getHeader(CallIdHeader.NAME);
                if (callIdHeader != null) {
                    Event subscribe = sipSubscribe.getOkSubscribe(callIdHeader.getCallId());
                    if (subscribe != null) {
                        EventResult eventResult = new EventResult(responseEvent);
                        sipSubscribe.removeOkSubscribe(callIdHeader.getCallId());
                        subscribe.response(eventResult);
                    }
                }
            }
        } else if ((status >= Response.TRYING) && (status < Response.OK)) {
            // 增加其它无需回复的响应，如101、180等
        } else {
            logger.warn("接收到失败的response响应！status：" + status + ",message:" + response.getReasonPhrase());
            if (responseEvent.getResponse() != null && sipSubscribe.getErrorSubscribesSize() > 0) {
                CallIdHeader callIdHeader = (CallIdHeader)responseEvent.getResponse().getHeader(CallIdHeader.NAME);
                if (callIdHeader != null) {
                    Event subscribe = sipSubscribe.getErrorSubscribe(callIdHeader.getCallId());
                    if (subscribe != null) {
                        EventResult eventResult = new EventResult(responseEvent);
                        subscribe.response(eventResult);
                        sipSubscribe.removeErrorSubscribe(callIdHeader.getCallId());
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
        logger.info("[消息发送超时]");
        ClientTransaction clientTransaction = timeoutEvent.getClientTransaction();

        if (clientTransaction != null) {
            logger.info("[发送错误订阅] clientTransaction != null");
            Request request = clientTransaction.getRequest();
            if (request != null) {
                logger.info("[发送错误订阅] request != null");
                CallIdHeader callIdHeader = (CallIdHeader)request.getHeader(CallIdHeader.NAME);
                if (callIdHeader != null) {
                    logger.info("[发送错误订阅]");
                    Event subscribe = sipSubscribe.getErrorSubscribe(callIdHeader.getCallId());
                    EventResult eventResult = new EventResult(timeoutEvent);
                    if (subscribe != null) {
                        subscribe.response(eventResult);
                    }
                    sipSubscribe.removeOkSubscribe(callIdHeader.getCallId());
                    sipSubscribe.removeErrorSubscribe(callIdHeader.getCallId());
                }
            }
        }
        eventPublisher.requestTimeOut(timeoutEvent);
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

        // sipSubscribe.getErrorSubscribe(header.getCallId()).response(terminatedEventEventResult);
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        CallIdHeader callId = dialogTerminatedEvent.getDialog().getCallId();
    }

}
