package io.github.lunasaw.gbproxy.client.user;

import javax.sip.RequestEvent;

import com.luna.common.os.SystemInfoUtil;

import gov.nist.javax.sip.message.SIPRequest;
import io.github.lunasaw.sip.common.entity.Device;
import io.github.lunasaw.sip.common.entity.FromDevice;
import io.github.lunasaw.sip.common.utils.SipUtils;

/**
 * @author luna
 * @date 2023/12/29
 */
public class CustomSipUserGenerateClient implements SipUserGenerateClient {
    public static final String LOOP_IP    = SystemInfoUtil.getIpv4();
    private static FromDevice  fromDevice = null;

    static {
        fromDevice = FromDevice.getInstance("33010602011187000001", LOOP_IP, 8118);
    }

    @Override
    public Device getToDevice(String userId) {
        return DeviceClientConfig.DEVICE_CLIENT_VIEW_MAP.get(userId);
    }

    @Override
    public Device getFromDevice() {
        return fromDevice;
    }

    @Override
    public boolean checkDevice(RequestEvent evt) {
        SIPRequest request = (SIPRequest)evt.getRequest();

        // 在客户端看来 收到请求的时候fromHeader还是服务端的 toHeader才是自己的，这里是要查询自己的信息
        String userId = SipUtils.getUserIdFromToHeader(request);

        // 获取设备
        FromDevice fromDevice = (FromDevice)getFromDevice();

        if (fromDevice == null) {
            return false;
        }
        return userId.equals(fromDevice.getUserId());
    }
}
