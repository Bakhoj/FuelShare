package com.example.anders.fuelshare.common;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * BlueTooth Handler
 * Created by anders on 28-10-2015.
 *
 * Handles and keeps the bluetooth connection for the whole application
 */
public class BTH {
    BluetoothAdapter mBluetoothAdapter = null;

    public BTH(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            System.out.println("System doesn't support Bluetooth");
            return;
        }
    }

    public boolean isBtEnabled(){
        return mBluetoothAdapter.isEnabled();
    }

    public boolean checkConnection(){
        return true;
    }
}
