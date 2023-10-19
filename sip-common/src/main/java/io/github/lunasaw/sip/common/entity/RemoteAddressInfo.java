package io.github.lunasaw.sip.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luna
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemoteAddressInfo {
    private String ip;
    private Integer port;

}
