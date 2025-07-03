package io.github.lunasaw.sip.common.exception;

/**
 * SIP处理器异常
 * 用于SIP消息处理器执行过程中的异常处理
 *
 * @author luna
 * @date 2024/01/01
 */
public class SipProcessorException extends SipException {

    private static final long serialVersionUID = 1L;

    /**
     * 处理器名称
     */
    private final String      processorName;

    /**
     * SIP方法
     */
    private final String      sipMethod;

    public SipProcessorException(String processorName, String sipMethod, String message) {
        super(SipErrorType.SYSTEM_INTERNAL, "PROCESSOR_ERROR", message);
        this.processorName = processorName;
        this.sipMethod = sipMethod;
    }

    public SipProcessorException(String processorName, String sipMethod, String message, Throwable cause) {
        super(SipErrorType.SYSTEM_INTERNAL, "PROCESSOR_ERROR", message, cause);
        this.processorName = processorName;
        this.sipMethod = sipMethod;
    }

    public String getProcessorName() {
        return processorName;
    }

    public String getSipMethod() {
        return sipMethod;
    }

    @Override
    public String toString() {
        return String.format("SipProcessorException{processorName='%s', sipMethod='%s', errorType=%s, errorCode='%s', message='%s'}",
            processorName, sipMethod, getErrorType(), getErrorCode(), getMessage());
    }
}