package io.github.lunasaw.sip.common.async;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 异步SIP消息处理器 - 优化消息处理性能
 *
 * @author luna
 * @date 2024/1/6
 */
@Slf4j
@Component
public class AsyncSipMessageProcessor {

    private final ThreadPoolTaskExecutor messageExecutor;
    private final BlockingQueue<SipMessageTask> messageQueue = new LinkedBlockingQueue<>(1000);
    private final AtomicBoolean processing = new AtomicBoolean(false);

    public AsyncSipMessageProcessor(@Qualifier("sipMessageProcessor") ThreadPoolTaskExecutor messageExecutor) {
        this.messageExecutor = messageExecutor;
        startQueueProcessor();
    }

    /**
     * 异步处理请求消息
     */
    @Async("sipMessageProcessor")
    public CompletableFuture<Void> processRequestAsync(RequestEvent requestEvent) {
        return CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                // 实际的消息处理逻辑将由具体的处理器实现
                log.debug("Processing request: {} from {}", 
                    requestEvent.getRequest().getMethod(),
                    requestEvent.getRequest().getHeader("From"));
                
                long processingTime = System.currentTimeMillis() - startTime;
                if (processingTime > 100) { // 如果处理时间超过100ms，记录警告
                    log.warn("Slow request processing: {}ms for method {}", 
                        processingTime, requestEvent.getRequest().getMethod());
                }
            } catch (Exception e) {
                log.error("Failed to process request async", e);
            }
        }, messageExecutor);
    }

    /**
     * 异步处理响应消息
     */
    @Async("sipMessageProcessor")
    public CompletableFuture<Void> processResponseAsync(ResponseEvent responseEvent) {
        return CompletableFuture.runAsync(() -> {
            try {
                long startTime = System.currentTimeMillis();
                // 实际的消息处理逻辑将由具体的处理器实现
                log.debug("Processing response: {} status {}", 
                    responseEvent.getResponse().getHeader("CSeq"),
                    responseEvent.getResponse().getStatusCode());
                
                long processingTime = System.currentTimeMillis() - startTime;
                if (processingTime > 100) {
                    log.warn("Slow response processing: {}ms for status {}", 
                        processingTime, responseEvent.getResponse().getStatusCode());
                }
            } catch (Exception e) {
                log.error("Failed to process response async", e);
            }
        }, messageExecutor);
    }

    /**
     * 将消息加入队列处理
     */
    public boolean enqueueMessage(SipMessageTask task) {
        boolean queued = messageQueue.offer(task);
        if (queued) {
            processMessages();
        } else {
            log.warn("Message queue is full, rejecting message: {}", task.getTaskId());
        }
        return queued;
    }

    /**
     * 批量处理消息队列
     */
    private void processMessages() {
        if (processing.compareAndSet(false, true)) {
            messageExecutor.execute(() -> {
                try {
                    int processedCount = 0;
                    long startTime = System.currentTimeMillis();
                    
                    while (!messageQueue.isEmpty() && processedCount < 100) { // 每批最多处理100条消息
                        SipMessageTask task = messageQueue.poll();
                        if (task != null) {
                            try {
                                task.execute();
                                processedCount++;
                            } catch (Exception e) {
                                log.error("Failed to execute message task: {}", task.getTaskId(), e);
                            }
                        }
                    }
                    
                    if (processedCount > 0) {
                        long processingTime = System.currentTimeMillis() - startTime;
                        log.debug("Batch processed {} messages in {}ms", processedCount, processingTime);
                    }
                } finally {
                    processing.set(false);
                    // 检查是否有新消息进来
                    if (!messageQueue.isEmpty()) {
                        processMessages();
                    }
                }
            });
        }
    }

    /**
     * 启动队列处理器
     */
    private void startQueueProcessor() {
        log.info("Starting async SIP message processor");
    }

    /**
     * 获取队列状态
     */
    public MessageQueueStatus getQueueStatus() {
        return new MessageQueueStatus(
            messageQueue.size(),
            messageQueue.remainingCapacity(),
            processing.get()
        );
    }

    /**
     * SIP消息任务接口
     */
    public interface SipMessageTask {
        String getTaskId();
        void execute();
        TaskType getType();
    }

    /**
     * 任务类型枚举
     */
    public enum TaskType {
        REQUEST, RESPONSE, TIMEOUT
    }

    /**
     * 消息队列状态
     */
    public static class MessageQueueStatus {
        private final int queueSize;
        private final int remainingCapacity;
        private final boolean processing;

        public MessageQueueStatus(int queueSize, int remainingCapacity, boolean processing) {
            this.queueSize = queueSize;
            this.remainingCapacity = remainingCapacity;
            this.processing = processing;
        }

        public int getQueueSize() { return queueSize; }
        public int getRemainingCapacity() { return remainingCapacity; }
        public boolean isProcessing() { return processing; }

        @Override
        public String toString() {
            return String.format("MessageQueueStatus{queueSize=%d, remainingCapacity=%d, processing=%s}", 
                queueSize, remainingCapacity, processing);
        }
    }

    /**
     * 请求消息任务实现
     */
    public static class RequestMessageTask implements SipMessageTask {
        private final String taskId;
        private final RequestEvent requestEvent;
        private final Runnable processor;

        public RequestMessageTask(String taskId, RequestEvent requestEvent, Runnable processor) {
            this.taskId = taskId;
            this.requestEvent = requestEvent;
            this.processor = processor;
        }

        @Override
        public String getTaskId() { return taskId; }

        @Override
        public void execute() { processor.run(); }

        @Override
        public TaskType getType() { return TaskType.REQUEST; }

        public RequestEvent getRequestEvent() { return requestEvent; }
    }

    /**
     * 响应消息任务实现
     */
    public static class ResponseMessageTask implements SipMessageTask {
        private final String taskId;
        private final ResponseEvent responseEvent;
        private final Runnable processor;

        public ResponseMessageTask(String taskId, ResponseEvent responseEvent, Runnable processor) {
            this.taskId = taskId;
            this.responseEvent = responseEvent;
            this.processor = processor;
        }

        @Override
        public String getTaskId() { return taskId; }

        @Override
        public void execute() { processor.run(); }

        @Override
        public TaskType getType() { return TaskType.RESPONSE; }

        public ResponseEvent getResponseEvent() { return responseEvent; }
    }
}