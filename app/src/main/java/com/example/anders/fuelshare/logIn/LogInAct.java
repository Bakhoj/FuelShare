package com.example.anders.fuelshare.logIn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anders.fuelshare.PEDO.PEDOact;
import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.data.AsyncLoginDatabase;
import com.example.anders.fuelshare.data.BTH;
import com.example.anders.fuelshare.map.MapAct;


public class LogInAct extends Activity implements View.OnClickListener{

    BTH bth = BTH.getInstance();
    Button logBtn;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        bth.startBt(this);

        //login button listener
        logBtn = (Button) findViewById(R.id.login_ok_but);
        logBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        AsyncLoginDatabase aLD = new AsyncLoginDatabase(this);
        aLD.execute();
    }

    public void login(){
        /*
        starts PEDO if AsyncLoginDatabase allows it
         */
        i = new Intent(this, PEDOact.class);
        this.startActivity(i);
        finish();
    }

    public void loginFail(){
        /*
            Add popup window or something telling the user
            that the user and password combination wasn't found
         */
    }
}
