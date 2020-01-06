package com.guider.health.bluetooth.core;

import java.util.UUID;

/**
 * Created by haix on 2019/6/10.
 */

public interface DeviceUUID {

    UUID getSERVICE_UUID();
    UUID getCHARACTER_UUID();
    UUID getDATA_LINE_UUID();
    UUID getUUID_FINAL();
}
