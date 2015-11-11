package com.example.anders.fuelshare.PEDO;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.anders.fuelshare.R;
import com.example.anders.fuelshare.data.BTH;
import com.example.anders.fuelshare.data.Constants;

public class PEDOact extends Activity implements View.OnClickListener{

    Handler mHandler;
    TextView distance, battery, usage;
    Button btn;
    BTH bth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pedo);

        distance = (TextView) findViewById(R.id.pedo_test_distance);
        battery = (TextView) findViewById(R.id.pedo_test_battery_level);
        usage = (TextView) findViewById(R.id.pedo_test_usage);
        btn = (Button) findViewById(R.id.pedo_test_btn);

        distance.setText("Distance traveled: \t0");
        battery.setText("Battery level: \t\t\t\t0");
        usage.setText("use/distance: \t\t\t\t0");

        btn.setOnClickListener(this);
    }

    /**
     * updates all the numbers on the display.
     * should be called everytime new data has been read.
     */
    private void updateUI(){
        System.out.println("Looking for input data");
        new Thread(new Runnable(){
            public void run(){
                while(true){
                    if(mHandler.hasMessages(Constants.MESSAGE_READ)) {
                        System.out.println(mHandler.obtainMessage(Constants.MESSAGE_READ).toString());
                    } else {
                        System.out.println("Nothing from Input");
                    }
                }
            }
        }).start();

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
        bth = BTH.getInstance();
        //bth.connectBT();
        bth.testMethod(bth.connectBT());
        mHandler = bth.getHandler();
        updateUI();
    }
}
