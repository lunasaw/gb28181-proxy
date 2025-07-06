package io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl;

import com.luna.common.check.Assert;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.AbstractClientCommandStrategy;
import io.github.lunasaw.sip.common.transmit.event.Event;
import lombok.extern.slf4j.Slf4j;

/**
 * 注册命令策略实现
 * 处理设备注册和注销相关命令
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public class RegisterCommandStrategy extends AbstractClientCommandStrategy {

    @Override
    protected String buildCommandContent(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        // 注册命令不需要构建XML内容，直接使用SipSender发送REGISTER请求
        return null;
    }

    @Override
    protected String sendCommand(FromDevice fromDevice, ToDevice toDevice, String content, Event errorEvent, Event okEvent) {
        // 注册命令使用特殊的发送方式
        Integer expires = getExpiresFromParams();
        // 使用带两个事件参数的方法，确保okEvent也能正确传递
        return SipSender.doRegisterRequest(fromDevice, toDevice, expires, toDevice.getCallId(), errorEvent, okEvent);
    }

    @Override
    public String getCommandType() {
        return "REGISTER";
    }

    @Override
    public String getCommandDescription() {
        return "设备注册/注销";
    }

    @Override
    protected void validateParams(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        super.validateParams(fromDevice, toDevice, params);

        if (params.length == 0) {
            throw new IllegalArgumentException("注册命令需要提供过期时间参数");
        }

        Object param = params[0];
        Assert.notNull(param, "过期时间不能为空");

        if (!(param instanceof Integer)) {
            throw new IllegalArgumentException("过期时间参数必须是Integer类型");
        }

        Integer expires = (Integer) param;
        if (expires < 0) {
            throw new IllegalArgumentException("过期时间不能为负数");
        }
    }

    /**
     * 从参数中获取过期时间
     *
     * @return 过期时间
     */
    private Integer getExpiresFromParams() {
        return 3600; // 默认1小时
    }
}