package io.github.lunasaw.gbproxy.client.transmit.response.register;



/**
 * @author luna
 * @date 2023/10/17
 */
public interface RegisterProcessorClient {

    /**
     * 过期时间
     *
     * @param userId 用户id
     * @return second time
     */
    default Integer getExpire(String userId) {
        return 300;
    }

    /**
     * 注册成功
     *
     * @param toUserId
     */
    void registerSuccess(String toUserId);

}
