package io.github.lunasaw.gbproxy.test.user.client;

import java.util.Map;
import java.util.concurrent.*;

import io.github.lunasaw.gbproxy.test.config.DeviceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.client.transmit.cmd.ClientSendCmd;
import io.github.lunasaw.gbproxy.client.transmit.response.register.RegisterProcessorClient;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.entity.ToDevice;

/**
 * @author luna
 * @date 2023/10/17
 */
@Component
public class DefaultRegisterProcessorClient implements RegisterProcessorClient {

    public static Boolean isRegister = true;
    public static Map<String, Device> deviceMap = new ConcurrentHashMap<>();
    ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool(1);
    @Autowired
    @Qualifier("clientFrom")
    private Device fromDevice;
    @Qualifier("sipTaskExecutor")
    @Autowired
    private TaskExecutor sipTaskExecutor;

    @Override
    public Integer getExpire(String userId) {
        return isRegister ? 300 : 0;
    }

    @Override
    public void registerSuccess(String toUserId) {
        // 定时任务 每分钟执行一次
        ScheduledFuture<?> future = taskExecutor.scheduleWithFixedDelay(
                () -> {
                    if (!isRegister) {
                        return;
                    }
                    ClientSendCmd.deviceKeepLiveNotify((FromDevice) fromDevice, (ToDevice) DeviceConfig.DEVICE_CLIENT_VIEW_MAP.get(toUserId), "OK");
                }, 60, 90, TimeUnit.SECONDS);

        if (!isRegister) {
            // 注销
            future.cancel(false);
        }
    }

    @Override
    public Device getToDevice(String userId) {
        return deviceMap.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }
}
