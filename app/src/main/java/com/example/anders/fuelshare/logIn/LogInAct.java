package com.example.anders.fuelshare.logIn;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.anders.fuelshare.PEDO.PEDOact;
import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.data.BTH;
import com.example.anders.fuelshare.map.MapAct;


public class LogInAct extends Activity implements View.OnClickListener{

    BTH bth = BTH.getInstance();
    Button logBtn, createBtn;
    Intent i;                   // dårlig idea at smide den her op, så allocater du plads til en intent uden at vide om du skal bruge den endnu
    EditText ETemail, ETpass;
    String sEmail, sPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        bth.startBt(this);

        //login button listener
        logBtn = (Button) findViewById(R.id.login_ok_but);
        logBtn.setOnClickListener(this);

        //create button listener
        createBtn = (Button) findViewById(R.id.login_create_but);
        createBtn.setOnClickListener(this);
    }

    private boolean checkLogIn(){

        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_ok_but:
                if(login()) {
                    i = new Intent(this, MapAct.class);
                    this.startActivity(i);
                    finish();
                }
                else{
                    loginFail();
                }
                break;
            case R.id.login_create_but:
                System.out.println("Create");
                i = new Intent(this, PEDOact.class);
                this.startActivity(i);
                finish();
                break;
        }
    }

    private boolean login(){
        /*
            check if the user and password fit with the Database
            right now it just puts the email and password into strings
         */
        sEmail = ETemail.getText().toString();
        sPass = ETpass.getText().toString();
        return true;
    }

    private void loginFail(){
        /*
            Add popup window or something telling the user
            that the user and password combination wasn't found
         */
    }
}
