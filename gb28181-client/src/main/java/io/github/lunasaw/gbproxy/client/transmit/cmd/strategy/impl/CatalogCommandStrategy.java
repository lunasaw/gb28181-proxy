package io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.impl;

import com.luna.common.check.Assert;
import com.luna.common.text.RandomStrUtil;

import io.github.lunasaw.gb28181.common.entity.response.DeviceResponse;
import io.github.lunasaw.gb28181.common.entity.response.DeviceItem;
import io.github.lunasaw.gb28181.common.entity.enums.CmdTypeEnum;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.gbproxy.client.transmit.cmd.strategy.AbstractClientCommandStrategy;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * 设备目录命令策略实现
 * 处理设备目录查询响应相关命令
 *
 * @author luna
 * @date 2024/01/01
 */
@Slf4j
public class CatalogCommandStrategy extends AbstractClientCommandStrategy {

    @Override
    protected String buildCommandContent(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        if (params.length == 0) {
            throw new IllegalArgumentException("目录命令需要提供设备信息参数");
        }

        Object param = params[0];
        if (param instanceof DeviceResponse) {
            // 直接使用设备响应对象
            return ((DeviceResponse) param).toString();
        } else if (param instanceof List) {
            // 使用设备列表构建设备响应
            @SuppressWarnings("unchecked")
            List<DeviceItem> deviceItems = (List<DeviceItem>) param;
            DeviceResponse deviceResponse = new DeviceResponse(
                    CmdTypeEnum.CATALOG.getType(),
                    generateSn(),
                    getDeviceId(fromDevice)
            );
            deviceResponse.setSumNum(deviceItems.size());
            deviceResponse.setDeviceItemList(deviceItems);
            return deviceResponse.toString();
        } else if (param instanceof DeviceItem) {
            // 使用单个设备项构建设备响应
            DeviceItem deviceItem = (DeviceItem) param;
            DeviceResponse deviceResponse = new DeviceResponse(
                    CmdTypeEnum.CATALOG.getType(),
                    generateSn(),
                    getDeviceId(fromDevice)
            );
            deviceResponse.setSumNum(1);
            deviceResponse.setDeviceItemList(Lists.newArrayList(deviceItem));
            return deviceResponse.toString();
        } else {
            throw new IllegalArgumentException("不支持的目录参数类型: " + param.getClass().getSimpleName());
        }
    }

    @Override
    public String getCommandType() {
        return CmdTypeEnum.CATALOG.getType();
    }

    @Override
    public String getCommandDescription() {
        return "设备目录查询响应";
    }

    @Override
    protected void validateParams(FromDevice fromDevice, ToDevice toDevice, Object... params) {
        super.validateParams(fromDevice, toDevice, params);

        if (params.length == 0) {
            throw new IllegalArgumentException("目录命令需要提供设备信息参数");
        }

        Object param = params[0];
        Assert.notNull(param, "设备信息不能为空");

        if (!(param instanceof DeviceResponse) && !(param instanceof List) && !(param instanceof DeviceItem)) {
            throw new IllegalArgumentException("目录参数必须是DeviceResponse、List<DeviceItem>或DeviceItem类型");
        }

        if (param instanceof List) {
            @SuppressWarnings("unchecked")
            List<?> list = (List<?>) param;
            if (!list.isEmpty() && !(list.get(0) instanceof DeviceItem)) {
                throw new IllegalArgumentException("列表参数必须包含DeviceItem类型的元素");
            }
        }
    }
}