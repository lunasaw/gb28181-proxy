package io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl;

import com.luna.common.check.Assert;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceKeepLiveNotify;
import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.AbstractClientCommandStrategy;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳命令策略实现
 * 处理设备心跳状态上报相关命令
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public class KeepaliveCommandStrategy extends AbstractClientCommandStrategy {

    @Override
    protected String buildCommandContent(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        if (params.length == 0) {
            throw new IllegalArgumentException("心跳命令需要提供状态参数");
        }

        String status = (String) params[0];
        Assert.notNull(status, "心跳状态不能为空");

        DeviceKeepLiveNotify deviceKeepLiveNotify = new DeviceKeepLiveNotify(
                CmdTypeEnum.KEEPALIVE.getType(),
                generateSn(),
                getDeviceId(fromDevice)
        );
        deviceKeepLiveNotify.setStatus(status);

        return deviceKeepLiveNotify.toString();
    }

    @Override
    public String getCommandType() {
        return CmdTypeEnum.KEEPALIVE.getType();
    }

    @Override
    public String getCommandDescription() {
        return "设备心跳状态上报";
    }

    @Override
    protected void validateParams(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        super.validateParams(fromDevice, toDevice, params);

        if (params.length == 0) {
            throw new IllegalArgumentException("心跳命令需要提供状态参数");
        }

        Object param = params[0];
        Assert.notNull(param, "心跳状态不能为空");

        if (!(param instanceof String)) {
            throw new IllegalArgumentException("心跳状态参数必须是String类型");
        }
    }
}