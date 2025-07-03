package io.github.lunasaw.sip.common.exception;

/**
 * SIP错误类型枚举
 * 定义SIP协议处理过程中可能出现的各种错误类型
 *
 * @author luna
 * @date 2024/01/01
 */
public enum SipErrorType {

    /**
     * 未知错误
     */
    UNKNOWN("未知错误"),

    /**
     * 协议解析错误
     */
    PROTOCOL_PARSE("协议解析错误"),

    /**
     * 消息格式错误
     */
    MESSAGE_FORMAT("消息格式错误"),

    /**
     * 网络连接错误
     */
    NETWORK_CONNECTION("网络连接错误"),

    /**
     * 认证失败
     */
    AUTHENTICATION_FAILED("认证失败"),

    /**
     * 权限不足
     */
    PERMISSION_DENIED("权限不足"),

    /**
     * 设备不存在
     */
    DEVICE_NOT_FOUND("设备不存在"),

    /**
     * 设备离线
     */
    DEVICE_OFFLINE("设备离线"),

    /**
     * 配置错误
     */
    CONFIGURATION_ERROR("配置错误"),

    /**
     * 超时错误
     */
    TIMEOUT("超时错误"),

    /**
     * 系统内部错误
     */
    SYSTEM_INTERNAL("系统内部错误"),

    /**
     * 资源不足
     */
    RESOURCE_INSUFFICIENT("资源不足");

    private final String description;

    SipErrorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}