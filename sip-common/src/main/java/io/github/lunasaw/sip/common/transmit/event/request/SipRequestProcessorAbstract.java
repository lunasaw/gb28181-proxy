package io.github.lunasaw.sip.common.transmit.event.request;

import java.util.concurrent.CompletableFuture;
import javax.sip.RequestEvent;

import io.github.lunasaw.sip.common.async.SipAsyncProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * SIP请求处理器抽象类
 * 提供异步处理能力的基础实现
 *
 * @author luna
 */
@Getter
@Setter
public abstract class SipRequestProcessorAbstract implements SipRequestProcessor, SipAsyncProcessor {

    /**
     * 是否启用异步处理
     */
    protected boolean asyncEnabled       = true;

    /**
     * 异步处理超时时间（毫秒）
     */
    protected long    asyncTimeoutMillis = 30000L;

    @Override
    public CompletableFuture<Void> processAsync(RequestEvent requestEvent) {
        if (!isAsyncSupported()) {
            // 如果不支持异步，则同步执行
            return CompletableFuture.runAsync(() -> process(requestEvent));
        }

        return CompletableFuture.runAsync(() -> process(requestEvent));
    }

    @Override
    public void processSync(RequestEvent requestEvent) {
        process(requestEvent);
    }

    @Override
    public boolean isAsyncSupported() {
        return asyncEnabled;
    }

    @Override
    public String getProcessorName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 设置是否启用异步处理
     *
     * @param asyncEnabled 是否启用异步
     */
    public void setAsyncEnabled(boolean asyncEnabled) {
        this.asyncEnabled = asyncEnabled;
    }

    /**
     * 设置异步处理超时时间
     *
     * @param timeoutMillis 超时时间（毫秒）
     */
    public void setAsyncTimeoutMillis(long timeoutMillis) {
        this.asyncTimeoutMillis = timeoutMillis;
    }
}
