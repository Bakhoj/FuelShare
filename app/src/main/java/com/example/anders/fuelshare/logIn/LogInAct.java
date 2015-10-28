package com.example.anders.fuelshare.logIn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.common.BTH;


public class LogInAct extends Activity {

    private final int REQUEST_ENABLE_BT = 27;
    BTH bth = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        startBth();
    }

    private void startBth(){
        bth = new BTH();
        if(!bth.isBtEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
