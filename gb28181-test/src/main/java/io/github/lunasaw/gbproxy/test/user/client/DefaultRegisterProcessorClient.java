package io.github.lunasaw.gbproxy.test.user.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;
import io.github.lunasaw.sip.common.utils.DynamicTask;
import io.github.lunasaw.sip.common.service.DeviceSupplier;
import lombok.extern.slf4j.Slf4j;

/**
 * @author luna
 * @date 2023/10/17
 */
@Slf4j
@Component
public class DefaultRegisterProcessorClient implements RegisterProcessorClient {

    private static final String                   KEEPALIVE    = "keepalive";
    /**
     * 心跳定时任务线程池
     */
    private static final ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(1);
    public static Boolean                         isRegister   = true;
    @Autowired
    @Qualifier("clientFrom")
    private Device                                fromDevice;

    @Autowired
    private DynamicTask                           dynamicTask;

    @Autowired
    private DeviceSupplier                        deviceSupplier;

    @Override
    public Integer getExpire(String userId) {
        return isRegister ? 300 : 0;
    }

    @Override
    public void registerSuccess(String toUserId) {
        // 定时任务 每分钟执行一次
        dynamicTask.startCron(KEEPALIVE,
            () -> {
                if (!isRegister) {
                    return;
                }
                ClientSendCmd.deviceKeepLiveNotify((FromDevice)fromDevice, (ToDevice)deviceSupplier.getDevice(toUserId), "OK",
                    eventResult -> {
                        dynamicTask.stop(KEEPALIVE);
                        // 注册
                        log.error("心跳失败 发起注册 registerSuccess::toUserId = {} ", toUserId);
                        ClientSendCmd.deviceRegister((FromDevice)fromDevice, (ToDevice)deviceSupplier.getDevice(toUserId), 300);
                    });
            }, 60, TimeUnit.SECONDS);

        if (!isRegister) {
            // 注销
            dynamicTask.stop(KEEPALIVE);
        }
    }
}
