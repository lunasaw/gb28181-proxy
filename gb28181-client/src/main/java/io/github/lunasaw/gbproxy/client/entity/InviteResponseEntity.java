package io.github.lunasaw.gbproxy.client.entity;

import java.util.Random;

/**
 * @author luna
 * @date 2023/11/6
 */
public class InviteResponseEntity {

    public static void main(String[] args) {

        StringBuffer content = getAckBody();

        System.out.println(content);
    }

    private static StringBuffer getAckBody(String sessionName) {
        StringBuffer content = new StringBuffer(200);
        content.append("v=0\r\n");
        content.append("o=" + "41010500002000000001" + " 0 0 IN IP4 " + "10.37.51.146" + "\r\n");
        content.append("s=" + "play" + "\r\n");
        content.append("c=IN IP4 " + "10.37.51.146" + "\r\n");
        if ("Playback".equalsIgnoreCase("play")) {
            content.append("t=" + "finalStartTime" + " " + "finalStopTime" + "\r\n");
        } else {
            content.append("t=0 0\r\n");
        }
        int localPort = 0;
        if (localPort == 0) {
            // 非严格模式端口不统一, 增加兼容性，修改为一个不为0的端口
            localPort = new Random().nextInt(65535) + 1;
        }
        content.append("m=video " + localPort + " RTP/AVP 96\r\n");
        content.append("a=sendonly\r\n");
        content.append("a=rtpmap:96 PS/90000\r\n");
        content.append("y=" + "ssrc" + "\r\n");
        content.append("f=\r\n");
        return content;
    }
}
