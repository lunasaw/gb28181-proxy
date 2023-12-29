package io.github.lunasaw.gbproxy.client.transmit.request.message;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gb28181.common.entity.notify.DeviceAlarmNotify;
import io.github.lunasaw.gb28181.common.entity.notify.DeviceBroadcastNotify;
import io.github.lunasaw.gb28181.common.entity.query.DeviceAlarmQuery;
import io.github.lunasaw.gb28181.common.entity.query.DeviceConfigDownload;
import io.github.lunasaw.gb28181.common.entity.query.DeviceRecordQuery;
import io.github.lunasaw.gb28181.common.entity.response.*;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
@ConditionalOnMissingBean(MessageProcessorClient.class)
public class CustomMessageProcessorClient implements MessageProcessorClient {
    @Override
    public DeviceRecord getDeviceRecord(DeviceRecordQuery deviceRecordQuery) {
        return null;
    }

    @Override
    public DeviceStatus getDeviceStatus(String userId) {
        return null;
    }

    @Override
    public DeviceInfo getDeviceInfo(String userId) {
        return null;
    }

    @Override
    public DeviceResponse getDeviceItem(String userId) {
        return null;
    }

    @Override
    public void broadcastNotify(DeviceBroadcastNotify broadcastNotify) {

    }

    @Override
    public DeviceAlarmNotify getDeviceAlarmNotify(DeviceAlarmQuery deviceAlarmQuery) {
        return null;
    }

    @Override
    public DeviceConfigResponse getDeviceConfigResponse(DeviceConfigDownload deviceConfigDownload) {
        return null;
    }

    @Override
    public <T> void deviceControl(T deviceControlBase) {

    }
}
