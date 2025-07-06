package io.github.lunasaw.gbproxy.client.transmit.cmd.strategy;

import com.luna.common.check.Assert;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.transmit.SipSender;
import io.github.lunasaw.sip.common.transmit.event.Event;
import lombok.extern.slf4j.Slf4j;

/**
 * 抽象客户端命令策略基类
 * 提供通用的命令执行逻辑和工具方法
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public abstract class AbstractClientCommandStrategy implements ClientCommandStrategy {

    @Override
    public String execute(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        return execute(fromDevice, toDevice, null, null, params);
    }

    @Override
    public String execute(FromDevice fromDevice, ToDevice toDevice, Event errorEvent, Event okEvent, Object... params) {
        try {
            log.debug("执行命令: {}, 发送设备: {}, 接收设备: {}", getCommandType(), fromDevice.getUserId(), toDevice.getUserId());

            // 参数校验
            validateParams(fromDevice, toDevice, params);

            // 构建命令内容
            String content = buildCommandContent(fromDevice, toDevice, params);

            // 发送命令
            String callId = sendCommand(fromDevice, toDevice, content, errorEvent, okEvent);

            log.debug("命令执行成功: {}, callId: {}", getCommandType(), callId);
            return callId;

        } catch (Exception e) {
            log.error("命令执行失败: {}, 错误信息: {}", getCommandType(), e.getMessage(), e);
            throw new RuntimeException("命令执行失败: " + getCommandType(), e);
        }
    }

    @Override
    public String executeWithSubscribe(FromDevice fromDevice, ToDevice toDevice, SubscribeInfo subscribeInfo, Object... params) {
        try {
            log.debug("执行订阅命令: {}, 发送设备: {}, 接收设备: {}", getCommandType(), fromDevice.getUserId(), toDevice.getUserId());

            // 参数校验
            validateParams(fromDevice, toDevice, params);
            Assert.notNull(subscribeInfo, "订阅信息不能为空");

            // 构建命令内容
            String content = buildCommandContent(fromDevice, toDevice, params);

            // 发送订阅命令
            String callId = sendSubscribeCommand(fromDevice, toDevice, content, subscribeInfo);

            log.debug("订阅命令执行成功: {}, callId: {}", getCommandType(), callId);
            return callId;

        } catch (Exception e) {
            log.error("订阅命令执行失败: {}, 错误信息: {}", getCommandType(), e.getMessage(), e);
            throw new RuntimeException("订阅命令执行失败: " + getCommandType(), e);
        }
    }

    /**
     * 参数校验
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @param params     参数
     */
    protected void validateParams(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        Assert.notNull(fromDevice, "发送设备不能为空");
        Assert.notNull(toDevice, "接收设备不能为空");
        Assert.notNull(fromDevice.getUserId(), "发送设备ID不能为空");
        Assert.notNull(toDevice.getUserId(), "接收设备ID不能为空");
    }

    /**
     * 构建命令内容
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @param params     参数
     * @return 命令内容
     */
    protected abstract String buildCommandContent(FromDevice fromDevice, ToDevice toDevice, Object... params);

    /**
     * 发送命令
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @param content    命令内容
     * @param errorEvent 错误事件
     * @param okEvent    成功事件
     * @return callId
     */
    protected String sendCommand(FromDevice fromDevice, ToDevice toDevice, String content, Event errorEvent, Event okEvent) {
        return SipSender.doMessageRequest(fromDevice, toDevice, content, errorEvent, okEvent);
    }

    /**
     * 发送订阅命令
     *
     * @param fromDevice    发送设备
     * @param toDevice      接收设备
     * @param content       命令内容
     * @param subscribeInfo 订阅信息
     * @return callId
     */
    protected String sendSubscribeCommand(FromDevice fromDevice, ToDevice toDevice, String content, SubscribeInfo subscribeInfo) {
        return SipSender.doNotifyRequest(fromDevice, toDevice, content, subscribeInfo);
    }

    /**
     * 生成随机序列号
     *
     * @return 序列号
     */
    protected String generateSn() {
        return RandomStrUtil.getValidationCode();
    }

    /**
     * 获取设备ID
     *
     * @param fromDevice 发送设备
     * @return 设备ID
     */
    protected String getDeviceId(FromDevice fromDevice) {
        return fromDevice.getUserId();
    }
}