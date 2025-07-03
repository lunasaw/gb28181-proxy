package io.github.lunasaw.sip.common.exception;

/**
 * SIP配置异常
 * 用于SIP配置相关的异常处理
 *
 * @author luna
 * @date 2024/01/01
 */
public class SipConfigurationException extends SipException {

    private static final long serialVersionUID = 1L;

    /**
     * 配置项名称
     */
    private final String      configKey;

    public SipConfigurationException(String configKey, String message) {
        super(SipErrorType.CONFIGURATION_ERROR, "CONFIG_ERROR", message);
        this.configKey = configKey;
    }

    public SipConfigurationException(String configKey, String message, Throwable cause) {
        super(SipErrorType.CONFIGURATION_ERROR, "CONFIG_ERROR", message, cause);
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }

    @Override
    public String toString() {
        return String.format("SipConfigurationException{configKey='%s', errorType=%s, errorCode='%s', message='%s'}",
            configKey, getErrorType(), getErrorCode(), getMessage());
    }
}