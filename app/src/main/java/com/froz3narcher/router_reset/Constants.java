package com.froz3narcher.router_reset;

import java.util.UUID;

/**
 * Created by froz3narcher on 12/17/16.
 */

public class Constants
{
    // ref: https://developer.android.com/reference/android/bluetooth/BluetoothDevice.html
    // #createRfcommSocketToServiceRecord(java.util.UUID)
    // There's a Hint on this page that says use this common UUID for standard Bluetooth serial boards,
    // which this is.
    static final UUID thisUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String used to pass data between Activities
    // Prepend com.froz3narcher.router_reset for unique-ness. Get into the habit of
    // doing this to avoid conflict with other Android apps
    static final String DEVICE_RESULT = "com.froz3narcher.router_reset.DEVICE_RESULT";

    static final int MAC_ADDRESS_SIZE = 17;  // size of MAC address string

    // Various Intent requests
    static final int REQUEST_ENABLE_BT = 1;
    static final int REQUEST_DEVICE_BT = 2;
    static final int REQUEST_DISCONNECT = 3;
    static final int REQUEST_CONNECT = 4;

    static final int MESSAGE_READ = 10;

    static final Integer RESET_COMMAND = 1;
}
