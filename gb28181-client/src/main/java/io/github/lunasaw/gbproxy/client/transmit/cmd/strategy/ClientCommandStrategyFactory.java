package io.github.lunasaw.gbproxy.client.transmit.cmd.strategy;

import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl.AlarmCommandStrategy;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl.CatalogCommandStrategy;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl.KeepaliveCommandStrategy;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl.RegisterCommandStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端命令策略工厂
 * 管理和获取不同类型的命令策略
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public class ClientCommandStrategyFactory {

    private static final Map<String, ClientCommandStrategy> STRATEGY_MAP = new ConcurrentHashMap<>();

    static {
        // 初始化默认策略
        STRATEGY_MAP.put(CmdTypeEnum.ALARM.getType(), new AlarmCommandStrategy());
        STRATEGY_MAP.put(CmdTypeEnum.CATALOG.getType(), new CatalogCommandStrategy());
        STRATEGY_MAP.put(CmdTypeEnum.KEEPALIVE.getType(), new KeepaliveCommandStrategy());
        STRATEGY_MAP.put(CmdTypeEnum.REGISTER.getType(), new RegisterCommandStrategy());


        log.info("客户端命令策略工厂初始化完成，已注册策略: {}", STRATEGY_MAP.keySet());
    }

    /**
     * 获取命令策略
     *
     * @param commandType 命令类型
     * @return 命令策略
     */
    public static ClientCommandStrategy getStrategy(String commandType) {
        ClientCommandStrategy strategy = STRATEGY_MAP.get(commandType);
        if (strategy == null) {
            throw new IllegalArgumentException("未找到命令策略: " + commandType);
        }
        return strategy;
    }

    /**
     * 获取告警命令策略
     *
     * @return 告警命令策略
     */
    public static ClientCommandStrategy getAlarmStrategy() {
        return getStrategy(CmdTypeEnum.ALARM.getType());
    }

    /**
     * 获取心跳命令策略
     *
     * @return 心跳命令策略
     */
    public static ClientCommandStrategy getKeepaliveStrategy() {
        return getStrategy(CmdTypeEnum.KEEPALIVE.getType());
    }

    /**
     * 获取注册命令策略
     *
     * @return 注册命令策略
     */
    public static ClientCommandStrategy getRegisterStrategy() {
        return getStrategy("REGISTER");
    }

    /**
     * 注册自定义策略
     *
     * @param commandType 命令类型
     * @param strategy    策略实现
     */
    public static void registerStrategy(String commandType, ClientCommandStrategy strategy) {
        STRATEGY_MAP.put(commandType, strategy);
        log.info("注册客户端命令策略: {} -> {}", commandType, strategy.getClass().getSimpleName());
    }

    /**
     * 移除策略
     *
     * @param commandType 命令类型
     */
    public static void removeStrategy(String commandType) {
        ClientCommandStrategy removed = STRATEGY_MAP.remove(commandType);
        if (removed != null) {
            log.info("移除客户端命令策略: {} -> {}", commandType, removed.getClass().getSimpleName());
        }
    }

    /**
     * 获取所有已注册的策略类型
     *
     * @return 策略类型集合
     */
    public static java.util.Set<String> getRegisteredCommandTypes() {
        return STRATEGY_MAP.keySet();
    }

    /**
     * 检查策略是否存在
     *
     * @param commandType 命令类型
     * @return 是否存在
     */
    public static boolean hasStrategy(String commandType) {
        return STRATEGY_MAP.containsKey(commandType);
    }
}