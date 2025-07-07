package io.github.lunasaw.gbproxy.client.transmit.cmd;

import com.luna.common.check.Assert;
import io.github.lunasaw.gb28181.common.entity.DeviceAlarm;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.sip.common.subscribe.SubscribeInfo;
import io.github.lunasaw.sip.common.transmit.event.Event;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.ClientCommandStrategy;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.ClientCommandStrategyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * GB28181客户端命令发送器
 * 使用策略模式和建造者模式，提供更灵活和可扩展的命令发送接口
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public class ClientCommandSender {

    // ==================== 策略模式命令发送 ====================

    /**
     * 使用策略模式发送命令
     *
     * @param commandType 命令类型
     * @param fromDevice  发送设备
     * @param toDevice    接收设备
     * @param params      命令参数
     * @return callId
     */
    public static String sendCommand(String commandType, FromDevice fromDevice, ToDevice toDevice, Object... params) {
        ClientCommandStrategy strategy = ClientCommandStrategyFactory.getStrategy(commandType);
        return strategy.execute(fromDevice, toDevice, params);
    }

    /**
     * 使用策略模式发送命令（带事件）
     *
     * @param commandType 命令类型
     * @param fromDevice  发送设备
     * @param toDevice    接收设备
     * @param errorEvent  错误事件
     * @param okEvent     成功事件
     * @param params      命令参数
     * @return callId
     */
    public static String sendCommand(String commandType, FromDevice fromDevice, ToDevice toDevice,
                                     Event errorEvent, Event okEvent, Object... params) {
        ClientCommandStrategy strategy = ClientCommandStrategyFactory.getStrategy(commandType);
        return strategy.execute(fromDevice, toDevice, errorEvent, okEvent, params);
    }

    /**
     * 使用策略模式发送订阅命令
     *
     * @param commandType   命令类型
     * @param fromDevice    发送设备
     * @param toDevice      接收设备
     * @param subscribeInfo 订阅信息
     * @param params        命令参数
     * @return callId
     */
    public static String sendSubscribeCommand(String commandType, FromDevice fromDevice, ToDevice toDevice,
                                              SubscribeInfo subscribeInfo, Object... params) {
        ClientCommandStrategy strategy = ClientCommandStrategyFactory.getStrategy(commandType);
        return strategy.executeWithSubscribe(fromDevice, toDevice, subscribeInfo, params);
    }

    // ==================== 便捷方法 ====================

    /**
     * 发送告警命令
     *
     * @param fromDevice  发送设备
     * @param toDevice    接收设备
     * @param deviceAlarm 告警信息
     * @return callId
     */
    public static String sendAlarmCommand(FromDevice fromDevice, ToDevice toDevice, DeviceAlarm deviceAlarm) {
        return sendCommand("Alarm", fromDevice, toDevice, deviceAlarm);
    }

    /**
     * 发送告警命令
     *
     * @param fromDevice        发送设备
     * @param toDevice          接收设备
     * @param deviceAlarmNotify 告警通知对象
     * @return callId
     */
    public static String sendAlarmCommand(FromDevice fromDevice, ToDevice toDevice, DeviceAlarmNotify deviceAlarmNotify) {
        return sendCommand("Alarm", fromDevice, toDevice, deviceAlarmNotify);
    }

    /**
     * 发送心跳命令
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @param status     状态信息
     * @return callId
     */
    public static String sendKeepaliveCommand(FromDevice fromDevice, ToDevice toDevice, String status) {
        return sendCommand("Keepalive", fromDevice, toDevice, status);
    }

    /**
     * 发送心跳命令（带事件）
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @param status     状态信息
     * @param errorEvent 错误事件
     * @param okEvent    成功事件
     * @return callId
     */
    public static String sendKeepaliveCommand(FromDevice fromDevice, ToDevice toDevice, String status,
                                              Event errorEvent, Event okEvent) {
        return sendCommand("Keepalive", fromDevice, toDevice, errorEvent, okEvent, status);
    }

    /**
     * 发送注册命令
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @param expires    过期时间
     * @return callId
     */
    public static String sendRegisterCommand(FromDevice fromDevice, ToDevice toDevice, Integer expires) {
        Assert.isTrue(expires >= 0, "过期时间应该 >= 0");
        return sendCommand("REGISTER", fromDevice, toDevice, expires);
    }

    /**
     * 发送注销命令
     *
     * @param fromDevice 发送设备
     * @param toDevice   接收设备
     * @return callId
     */
    public static String sendUnregisterCommand(FromDevice fromDevice, ToDevice toDevice) {
        return sendCommand("REGISTER", fromDevice, toDevice, 0);
    }

    // ==================== 建造者模式 ====================

    /**
     * 命令发送建造者
     * 提供流式API，支持链式调用
     */
    public static class CommandBuilder {
        private String commandType;
        private FromDevice fromDevice;
        private ToDevice toDevice;
        private Event errorEvent;
        private Event okEvent;
        private SubscribeInfo subscribeInfo;
        private Object[] params;

        public CommandBuilder commandType(String commandType) {
            this.commandType = commandType;
            return this;
        }

        public CommandBuilder fromDevice(FromDevice fromDevice) {
            this.fromDevice = fromDevice;
            return this;
        }

        public CommandBuilder toDevice(ToDevice toDevice) {
            this.toDevice = toDevice;
            return this;
        }

        public CommandBuilder errorEvent(Event errorEvent) {
            this.errorEvent = errorEvent;
            return this;
        }

        public CommandBuilder okEvent(Event okEvent) {
            this.okEvent = okEvent;
            return this;
        }

        public CommandBuilder subscribeInfo(SubscribeInfo subscribeInfo) {
            this.subscribeInfo = subscribeInfo;
            return this;
        }

        public CommandBuilder params(Object... params) {
            this.params = params;
            return this;
        }

        public String execute() {
            if (subscribeInfo != null) {
                return sendSubscribeCommand(commandType, fromDevice, toDevice, subscribeInfo, params);
            } else {
                return sendCommand(commandType, fromDevice, toDevice, errorEvent, okEvent, params);
            }
        }
    }

    /**
     * 创建命令建造者
     *
     * @return 命令建造者
     */
    public static CommandBuilder builder() {
        return new CommandBuilder();
    }

    // ==================== 工具方法 ====================

    /**
     * 获取所有已注册的命令类型
     *
     * @return 命令类型集合
     */
    public static java.util.Set<String> getRegisteredCommandTypes() {
        return ClientCommandStrategyFactory.getRegisteredCommandTypes();
    }

    /**
     * 检查命令类型是否已注册
     *
     * @param commandType 命令类型
     * @return 是否已注册
     */
    public static boolean hasCommandType(String commandType) {
        return ClientCommandStrategyFactory.hasStrategy(commandType);
    }

    /**
     * 注册自定义命令策略
     *
     * @param commandType 命令类型
     * @param strategy    策略实现
     */
    public static void registerCommandStrategy(String commandType, ClientCommandStrategy strategy) {
        ClientCommandStrategyFactory.registerStrategy(commandType, strategy);
    }
}