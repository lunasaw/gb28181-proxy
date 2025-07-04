package io.github.lunasaw.gbproxy.test.user.server.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.github.lunasaw.gbproxy.server.user.SipUserGenerateServer;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.service.DeviceSupplier;

/**
 * @author luna
 * @date 2023/12/29
 */
@Component
public class DefaultSipUserGenerateServer implements SipUserGenerateServer {

    @Autowired
    private DeviceSupplier deviceSupplier;

    @Override
    public Device getToDevice(String userId) {
        return deviceSupplier.getDevice(userId);
    }

    @Override
    public Device getFromDevice() {
        // 业务方自定义实现：这里可以根据实际业务需求返回服务端设备
        // 例如：从配置中获取、从数据库查询、从缓存获取等
        return deviceSupplier.getDevice("server"); // 假设服务端设备ID为"server"
    }
}
