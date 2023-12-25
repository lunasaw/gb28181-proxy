package io.github.lunasaw.gb28181.common.entity.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luna
 * @date 2023/10/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSession {

    String userId;
    String sipId;

}
