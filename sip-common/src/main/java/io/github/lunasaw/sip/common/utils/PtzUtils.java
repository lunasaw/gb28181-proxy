package io.github.lunasaw.sip.common.utils;

import io.github.lunasaw.sip.common.enums.PtzCmdEnum;

public class PtzUtils {

    public static String getPtzCmd(String ptzCmd, int speed) {
        return getPtzCmd(PtzCmdEnum.getByCommand(ptzCmd), speed);
    }

    public static String getPtzCmd(PtzCmdEnum ptzCmdEnum, int speed) {
        return getPtzCmd(ptzCmdEnum.getCmdCode(), speed, speed, speed);
    }

    /**
     * 获取控制命令代码
     *
     * @param cmdCode       命令code
     * @param horizonSpeed  水平速度
     * @param verticalSpeed 垂直速度
     * @param zoomSpeed     缩放速度
     * @return
     */
    public static String getPtzCmd(int cmdCode, int horizonSpeed, int verticalSpeed, int zoomSpeed) {
        StringBuilder builder = new StringBuilder("A50F01");

        String strTmp = String.format("%02X", cmdCode);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", horizonSpeed);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", verticalSpeed);
        builder.append(strTmp, 0, 2);
        //优化zoom变倍速率
        if ((zoomSpeed > 0) && (zoomSpeed < 16)) {
            zoomSpeed = 16;
        }
        strTmp = String.format("%X", zoomSpeed);
        builder.append(strTmp, 0, 1).append("0");
        //计算校验码
        int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + horizonSpeed + verticalSpeed + (zoomSpeed & 0XF0)) % 0X100;
        strTmp = String.format("%02X", checkCode);
        builder.append(strTmp, 0, 2);
        return builder.toString();
    }
}
