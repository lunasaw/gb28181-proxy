package io.github.lunasaw.gb28181.common.entity.utils;

import org.springframework.util.StringUtils;

/**
 * @author weidian
 */
public class GbUtil {

    public static String generateGbCode(Long id) {
        return generateGbCode("127.0.0.1", id);
    }

    public static String generateGbCode(String ip, Long id) {
        if (StringUtils.isEmpty(ip) || null == id) {
            return null;
        }
        // 将nvrId转成10位数字

        return getAreaCodeByIp(ip) + String.format("%010d", id);
    }

    public static String generateGbCode(String ip, String id) {
        if (StringUtils.isEmpty(ip) || null == id) {
            return null;
        }
        // 将nvrId转成10位数字

        return getAreaCodeByIp(ip) + id;

    }

    public static String getAreaCodeByIp(String ip) {
        /**
         * 33010602 (浙江杭州西湖区) 01(社区) 118 (NVR设备) 7(internel) 000001 (设备编码)
         *
         * 33010602011187000001
         */
        return "3301060201";
    }

    public static void main(String[] args) {
        System.out.println(generateGbCode("111", 2345L));
    }

    public static String getAreaByGbCode(String GbCode) {
        return StringUtils.isEmpty(GbCode) || GbCode.length() < 10 ? null : GbCode.substring(0, 10);
    }

}
