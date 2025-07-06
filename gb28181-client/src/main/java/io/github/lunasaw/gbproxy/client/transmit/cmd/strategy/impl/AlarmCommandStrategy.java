package io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl;

import com.luna.common.check.Assert;

import io.github.lunasaw.gb28181.common.entity.DeviceAlarm;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.AbstractClientCommandStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 告警命令策略实现
 * 处理设备告警上报相关命令
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public class AlarmCommandStrategy extends AbstractClientCommandStrategy {

    @Override
    protected String buildCommandContent(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        if (params.length == 0) {
            throw new IllegalArgumentException("告警命令需要提供告警信息参数");
        }

        Object param = params[0];
        if (param instanceof DeviceAlarmNotify) {
            // 直接使用告警通知对象
            return ((DeviceAlarmNotify) param).toString();
        } else if (param instanceof DeviceAlarm) {
            // 使用告警信息构建告警通知
            DeviceAlarm deviceAlarm = (DeviceAlarm) param;
            DeviceAlarmNotify deviceAlarmNotify = new DeviceAlarmNotify(
                    CmdTypeEnum.ALARM.getType(),
                    generateSn(),
                    getDeviceId(fromDevice)
            );
            deviceAlarmNotify.setAlarm(deviceAlarm);
            return deviceAlarmNotify.toString();
        } else {
            throw new IllegalArgumentException("不支持的告警参数类型: " + param.getClass().getSimpleName());
        }
    }

    @Override
    public String getCommandType() {
        return CmdTypeEnum.ALARM.getType();
    }

    @Override
    public String getCommandDescription() {
        return "设备告警上报";
    }

    @Override
    protected void validateParams(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        super.validateParams(fromDevice, toDevice, params);

        if (params.length == 0) {
            throw new IllegalArgumentException("告警命令需要提供告警信息参数");
        }

        Object param = params[0];
        Assert.notNull(param, "告警信息不能为空");

        if (!(param instanceof DeviceAlarmNotify) && !(param instanceof DeviceAlarm)) {
            throw new IllegalArgumentException("告警参数必须是DeviceAlarmNotify或DeviceAlarm类型");
        }
    }
}