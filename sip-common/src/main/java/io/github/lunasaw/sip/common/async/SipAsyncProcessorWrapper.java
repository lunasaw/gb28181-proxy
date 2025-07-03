package io.github.lunasaw.sip.common.async;

import java.util.concurrent.CompletableFuture;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

import io.github.lunasaw.sip.common.transmit.event.request.SipRequestProcessor;
import io.github.lunasaw.sip.common.transmit.event.response.SipResponseProcessor;

/**
 * SIP异步处理器包装器
 * 将现有的同步处理器包装为异步处理器，提供向后兼容性
 *
 * @author luna
 * @date 2024/01/01
 */
public class SipAsyncProcessorWrapper implements SipAsyncProcessor {

    private final SipRequestProcessor  requestProcessor;
    private final SipResponseProcessor responseProcessor;
    private final String               processorName;

    /**
     * 包装请求处理器
     */
    public SipAsyncProcessorWrapper(SipRequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
        this.responseProcessor = null;
        this.processorName = requestProcessor.getClass().getSimpleName();
    }

    /**
     * 包装响应处理器
     */
    public SipAsyncProcessorWrapper(SipResponseProcessor responseProcessor) {
        this.requestProcessor = null;
        this.responseProcessor = responseProcessor;
        this.processorName = responseProcessor.getClass().getSimpleName();
    }

    @Override
    public CompletableFuture<Void> processAsync(RequestEvent requestEvent) {
        if (requestProcessor == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> requestProcessor.process(requestEvent));
    }

    @Override
    public CompletableFuture<Void> processAsync(ResponseEvent responseEvent) {
        if (responseProcessor == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> responseProcessor.process(responseEvent));
    }

    @Override
    public void processSync(RequestEvent requestEvent) {
        if (requestProcessor != null) {
            requestProcessor.process(requestEvent);
        }
    }

    @Override
    public void processSync(ResponseEvent responseEvent) {
        if (responseProcessor != null) {
            responseProcessor.process(responseEvent);
        }
    }

    @Override
    public String getProcessorName() {
        return processorName;
    }

}