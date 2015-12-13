package com.example.anders.fuelshare.data;

import java.util.UUID;

/**
 * Created by anders on 10-Nov-15.
 *
 * think I'll delete  this one again
 */
public interface Constants {

    int REQUEST_ENABLE_BT = 27; //should just be > 0
    int MESSAGE_READ = 2;

    String DEVICE_NAME = "Can-Bus";
    String DEVICE_PASSWORD = "1234";
    int DEVICE_BAUDRATE = 115200;

    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    int STATE_INIT = 0;
    int STATE_CHARGE = 884;       // 0x374 (884)
    int STATE_ODOMETER = 1042;    // 0x412 (1042)
    int STATE_BREAKPEDAL = 520;   // 0x208 (520)
    int STATE_CHARGING = 905;          // 0x389 (905)
}
