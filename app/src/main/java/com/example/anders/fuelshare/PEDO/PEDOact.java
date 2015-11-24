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
import com.example.anders.fuelshare.common.LSH;
import com.example.anders.fuelshare.data.BTH;
import com.example.anders.fuelshare.data.Constants;
import com.google.android.gms.nearby.messages.Message;

/**
 * PEDO meter activity
 *
 * The activity shows the PEDO meter which will give the information from the car to the user.
 * basically PEDO UI logic
 */
public class PEDOact extends Activity implements View.OnClickListener{

    Handler mHandler;
    TextView distance, battery, usage;
    Button btn, btn2;
    BTH bth;
    LSH lsh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pedo);

        lsh = LSH.getInstance();

        distance = (TextView) findViewById(R.id.pedo_test_distance);
        battery = (TextView) findViewById(R.id.pedo_test_battery_level);
        usage = (TextView) findViewById(R.id.pedo_test_usage);
        btn = (Button) findViewById(R.id.pedo_test_btn);
        btn2 = (Button) findViewById(R.id.pedo_test_btn2);

        distance.setText("Distance traveled: \t0");
        battery.setText("Battery level: \t\t\t\t0");
        usage.setText("use/distance: \t\t\t\t0");

        btn.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    /**
     * updates all the numbers on the display.
     * should be called everytime new data has been read.
     */
    private void updateUI(){
        System.out.println("PRESSED");
        double dist = lsh.getDist();
        double bat = lsh.getLastBat();
        bat = (bat/2) -5;
        System.out.println("GOT DATA!");
        distance.setText("Distance traveled: \t" + dist);
        battery.setText("Battery level: \t\t\t\t" + bat + "%");
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
                bth = BTH.getInstance();
                //bth.connectBT();
                bth.testMethod(bth.connectBT());
                mHandler = bth.getHandler();
                break;
            case R.id.pedo_test_btn2:
                updateUI();
                break;
        }
    }
}
