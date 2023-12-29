package io.github.lunasaw.gbproxy.client.transmit.response.register;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.user.DeviceClientConfig;
import io.github.lunasaw.gbproxy.client.user.SipUserGenerateClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/17
 */
@Slf4j
public class CustomRegisterProcessorClient implements RegisterProcessorClient {

    /**
     * 心跳定时任务线程池
     */
    private static final ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(1);
    public static Boolean                         isRegister   = true;
    @Autowired
    private SipUserGenerateClient                 sipUserGenerateClient;

    @Override
    public Integer getExpire(String userId) {
        return isRegister ? 300 : 0;
    }

    @Override
    public void registerSuccess(String toUserId) {
        Device fromDevice = sipUserGenerateClient.getFromDevice();
        // 定时任务 每分钟执行一次
        ScheduledFuture<?> future = taskExecutor.scheduleWithFixedDelay(
            () -> {
                if (!isRegister) {
                    return;
                }
                ClientSendCmd.deviceKeepLiveNotify((FromDevice)fromDevice, (ToDevice)DeviceClientConfig.DEVICE_CLIENT_VIEW_MAP.get(toUserId), "OK",
                    eventResult -> {
                        // 注册
                        log.error("心跳失败 发起注册 registerSuccess::toUserId = {} ", toUserId);
                        ClientSendCmd.deviceRegister((FromDevice)fromDevice, (ToDevice)DeviceClientConfig.DEVICE_CLIENT_VIEW_MAP.get(toUserId), 300);
                    });
            }, 30, 60, TimeUnit.SECONDS);

        if (!isRegister) {
            // 注销
            future.cancel(false);
        }
    }
}
