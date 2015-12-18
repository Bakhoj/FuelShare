package com.example.anders.fuelshare.PEDO;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.data.AsyncBluetooth;
import com.example.anders.fuelshare.data.Logic;
import com.example.anders.fuelshare.map.MapAct;

/**
 * PEDO meter activity
 *
 * The activity shows the PEDO meter which will give the information from the car to the user.
 * basically PEDO UI logic
 */
public class PEDOact extends Activity implements View.OnClickListener{

    //Handler mHandler;
    int charging_buffer;
    TextView distance, battery, usage;
    ImageView batImage;
    Button btn, btn2, mapsBtn;
    CheckBox charging_cb, breaking_cb, turnedon_cb;
    AsyncBluetooth ab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pedo);

        charging_buffer = 0;
        distance = (TextView) findViewById(R.id.pedo_test_distance);
        battery = (TextView) findViewById(R.id.pedo_test_battery_level);
        usage = (TextView) findViewById(R.id.pedo_test_usage);
        btn = (Button) findViewById(R.id.pedo_test_btn);
        btn2 = (Button) findViewById(R.id.pedo_test_btn2);
        mapsBtn = (Button) findViewById(R.id.maps_btn);
        batImage = (ImageView) findViewById(R.id.pedo_test_bat_image);
        charging_cb = (CheckBox) findViewById(R.id.charging_checkBox);
        breaking_cb = (CheckBox) findViewById(R.id.break_checkBox);
        turnedon_cb = (CheckBox) findViewById(R.id.turnedon_checkBox);

        distance.setText("Distance traveled: \t0");
        battery.setText("Battery level: \t\t\t\t0");
        usage.setText("use/distance: \t\t\t\t0");

        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
        mapsBtn.setOnClickListener(this);
        if(savedInstanceState == null) {
            ab = new AsyncBluetooth(this);
        }
    }

    /**
     * updates all the numbers on the display.
     * should be called everytime new data has been read.
     */
    public void updateUI(){
        double bat = Logic.instance.getBatteryProcent();
        distance.setText(String.format("Distance traveled: \t%d", Logic.instance.getDistance()));
        battery.setText(String.format("Battery level: \t\t\t\t%s%%", bat));
        usage.setText(String.format("Velocity: \t\t\t\t%d", Logic.instance.getVelocity()));

        if(bat > 80) {
            batImage.setImageResource(R.drawable.battery_4);
        } else if(bat > 50) {
            batImage.setImageResource(R.drawable.battery_3);
        } else if(bat > 30) {
            batImage.setImageResource(R.drawable.battery_2);
        } else {
            batImage.setImageResource(R.drawable.battery_1);
        }
        if(Logic.instance.isTurnedOn()) {
            turnedon_cb.setChecked(true);
        } else {
            turnedon_cb.setChecked(false);
        }

        if(Logic.instance.charging) {
            charging_cb.setChecked(true);
            charging_buffer++;
            if(charging_buffer > 15){
                Logic.instance.charging = false;
                charging_buffer = 0;
            }
        } else {
            charging_cb.setChecked(false);
        }
        if(Logic.instance.isBrakePedal()) {
            breaking_cb.setChecked(true);
            breaking_cb.setText("Braking: " + Logic.instance.getBrakeCounter());
        } else {
            breaking_cb.setChecked(false);
        }
        Log.d("PEDOact", "UI has been updated");
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ab.cancel(true);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedoact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    /**
     * onClick(View)
     */
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.pedo_test_btn:
                //bth = BTH.getInstance();
                //bth.runAsync();
                ab.execute();
                /* bth = BTH.getInstance();
                //bth.connectBT();
                bth.testMethod(bth.connectBT());
                mHandler = bth.getHandler(); */
                break;
            case R.id.pedo_test_btn2:
                updateUI();
                break;
            case R.id.maps_btn:
                Intent i = new Intent(this, MapAct.class);
                this.startActivity(i);
                ab.cancel(true);
                finish();
                break;
        }
    }
}
