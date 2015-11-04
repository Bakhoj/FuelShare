package com.example.anders.fuelshare.common;

import android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.content.Intent;

/**
 * BlueTooth Handler
 * Created by anders on 28-10-2015.
 *
 * Handles and keeps the bluetooth connection for the whole application
 */
public class BTH {
    BluetoothAdapter mBluetoothAdapter = null;
    private final int REQUEST_ENABLE_BT = 27; //should just not be = 0

    private static BTH ourInstance = new BTH();
    public static BTH getInstace(){ return ourInstance;}
    private BTH(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            System.out.println("System doesn't support Bluetooth");
            return;
        }
    }

    public boolean startBt(Activity a){
        if(!isBtEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            a.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
    }
    public boolean isBtEnabled(){
        return mBluetoothAdapter.isEnabled();
    }
    public boolean checkConnection(){
        return true;
    }
}