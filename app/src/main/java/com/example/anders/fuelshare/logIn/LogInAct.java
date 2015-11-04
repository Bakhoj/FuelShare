package com.example.anders.fuelshare.logIn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.common.BTH;


public class LogInAct extends Activity implements View.OnClickListener{

    private final int REQUEST_ENABLE_BT = 27; //should just not be = 0
    BTH bth = null;
    Button logBtn, createBtn;
    Intent i;
    EditText ETemail, ETpass;
    String sEmail, sPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        startBth();

        //login button listener
        logBtn = (Button) findViewById(R.id.login_ok_but);
        logBtn.setOnClickListener(this);

        //create button listener
        createBtn = (Button) findViewById(R.id.login_create_but);
        createBtn.setOnClickListener(this);
    }

    private void startBth(){
        bth = new BTH();
        if(!bth.isBtEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private boolean checkLogIn(){

        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_ok_but:
                i = new Intent()
                System.out.println("Log In");
                break;
            case R.id.login_create_but:

                System.out.println("Create");
                break;
        }
    }
}
