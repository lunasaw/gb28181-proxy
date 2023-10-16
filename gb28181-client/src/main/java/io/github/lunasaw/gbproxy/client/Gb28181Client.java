package io.github.lunasaw.gbproxy.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author luna
 * @date 2023/10/11
 */
@SpringBootApplication
public class Gb28181Client {

    public static void main(String[] args) {
        SpringApplication.run(Gb28181Client.class, args);
    }
}
