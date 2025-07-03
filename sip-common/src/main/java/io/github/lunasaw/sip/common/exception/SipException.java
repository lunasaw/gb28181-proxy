package io.github.lunasaw.sip.common.exception;

/**
 * SIP异常基类
 * 提供统一的SIP协议相关异常处理
 *
 * @author luna
 * @date 2024/01/01
 */
public class SipException extends RuntimeException {

    private static final long  serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private final String       errorCode;

    /**
     * 错误类型
     */
    private final SipErrorType errorType;

    public SipException(String message) {
        this(SipErrorType.UNKNOWN, "SIP_ERROR", message);
    }

    public SipException(String message, Throwable cause) {
        this(SipErrorType.UNKNOWN, "SIP_ERROR", message, cause);
    }

    public SipException(SipErrorType errorType, String errorCode, String message) {
        super(message);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public SipException(SipErrorType errorType, String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public SipErrorType getErrorType() {
        return errorType;
    }

    @Override
    public String toString() {
        return String.format("SipException{errorType=%s, errorCode='%s', message='%s'}",
            errorType, errorCode, getMessage());
    }
}