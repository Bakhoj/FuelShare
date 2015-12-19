package com.example.anders.fuelshare.data;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import com.example.anders.fuelshare.PEDO.PEDOact;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by anders on 11-12-2015.
 *
 * Bluetooth Handling done with AsyncTask
 */
public class AsyncBluetooth extends AsyncTask<Void, Void, Void> {
    PEDOact mActivity;
    BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private final String DEVICE_NAME = "Can-Bus";
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private int distance, velocity, breakRead;

    private int outer_state, inner_state;
    private final int STATE_INIT = 0;
    private final int STATE_CHARGE = 884;       // 0x374 (884)
    private final int STATE_ODOMETER = 1042;    // 0x412 (1042)
    private final int STATE_BREAKPEDAL = 520;   // 0x208 (520)
    private final int STATE_CHARGING = 905;          // 0x389 (905)


    public AsyncBluetooth(){
    }

        public void setmActivity(Activity activity) {
            mActivity = (PEDOact) activity;
        }

        @Override
        protected void onPreExecute() {
            Logic.instance.asyncRunning = true;
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null){
                Log.d("AsyncTask", "System doesn't support Bluetooth");
                return null;
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    System.out.println(device.getName() + "\n" + device.getAddress());
                    if (device.getName().equals(DEVICE_NAME)) {
                        mmDevice = device;
                        break;
                    }
                }
            } else {
                //TODO: Make that toast!
                Log.d("AsyncTask", "No paired devices found");
                return null;
            }
            if(mmDevice == null) {
                Log.d("AsyncTask", "Can't find the right device");
                return null;
            }

            try{
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch(IOException e){ }

            mBluetoothAdapter.cancelDiscovery();

            try {
                //TODO: add while not connected, wait 5 seconds and try again a few times
                mmSocket.connect();
                System.out.println("Succes, socket is connected");
            } catch (IOException connectException) {
                try {
                    System.out.println("Failed to connect socket, trying to close socket ...");
                    mmSocket.close();
                    return null;
                } catch(IOException closeException) {
                    System.out.println("Failed to close connection socket.");
                    return null;
                }
            }

            InputStream tmpIn = null;
            DataInputStream mmInputstream = null;

            try {
                System.out.println("Making Inputstream ...");
                tmpIn = mmSocket.getInputStream();
                System.out.println("Done");
                mmInputstream = new DataInputStream(tmpIn);
            } catch(IOException e) {
                System.out.println("Failed making streams");
                return null;
            }
            int i = 0;
            while(!isCancelled()){
                try {
                    String input = mmInputstream.readLine();
//                    Log.d("AsyncTask", "InputRead: " + input.toString());
                    if (input.contains("ID:") && input.contains("Data:")) {
                        outer_state = STATE_INIT;
                        String[] first = input.split("  Data: ");
                        String id = first[0].replace("ID:", "");
                        id = id.replace(" ", "");
                        id = id.replace("\n", "");
                        String[] data = first[1].split(" ");
                        try {
                            stateMachine(Integer.parseInt(id, 16));
                        } catch (NumberFormatException e) {
                        }
                        for (String s : data) {
                            String st = s.replace(" ", "");
                            String str = st.replace("\n", "");
                            try {
                                stateMachine(Integer.parseInt(str, 16));
                            } catch (NumberFormatException e) {
                            }
                        }
                        if(i > 150) {
                            publishProgress();
                            i = 0;
                        }
                        i++;
                    } else {
                        Log.d("AsyncTask", "Could not identify read input");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Logic.instance.asyncRunning = false;
            mActivity.updateUI();
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            mActivity.updateUI();
            super.onProgressUpdate(values);
        }

    @Override
    protected void onCancelled() {
        Logic.instance.asyncRunning = false;
        mActivity.updateUI();
        super.onCancelled();
    }

    private void stateMachine(int input) {
            switch (outer_state) {
                case STATE_INIT:
                    inner_state = 0;
                    if (input == STATE_CHARGE ||
                            input == STATE_ODOMETER ||
                            input == STATE_CHARGING ||
                            input == STATE_BREAKPEDAL) {
                        outer_state = input;
                    }
                    break;
                case STATE_CHARGE:
                    switch (inner_state) {
                        case 0:
                            inner_state++;
                            break;
                        case 1:
                            Logic.instance.setBattery(input);
                            inner_state++;
                            break;
                        default:
                            if (inner_state < 7) {
                                inner_state++;
                                break;
                            }
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;

                case STATE_ODOMETER:
                    switch (inner_state) {
                        case 0:
                            //VELOCITY 1 of 2
                            velocity = input << 8;
                            inner_state++;
                            break;
                        case 1:
                            //VELOCITY 2 of 2
                            velocity += input;
                            Logic.instance.setVelocity(velocity);
                            inner_state++;
                            break;
                        case 2:
                            //DISTANCE 1 of 3
                            distance = input << 16;
                            inner_state++;
                            break;
                        case 3:
                            //DISTANCE 2 of 3
                            distance += input << 8;
                            inner_state++;
                            break;
                        case 4:
                            //DISTANCE 3 of 3
                            distance += input;
                            Logic.instance.setDistance(distance);
                            inner_state++;
                            break;
                        default:
                            if (inner_state < 7) {
                                inner_state++;
                                break;
                            }
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;
                case STATE_BREAKPEDAL:
                    switch (inner_state) {
                        case 0:
                            inner_state++;
                            break;
                        case 1:
                            inner_state++;
                            break;
                        case 2:
                            //BREAK 1 of 2
                            breakRead = input << 8;
                            inner_state++;
                            break;
                        case 3:
                            //BREAK 2 of 2
                            breakRead += input;
                            if (breakRead <= 24576) {
                                Logic.instance.setBrakePedal(false);
                                Log.d("AsyncTask", "break false");
                            } else {
                                Logic.instance.setBrakePedal(true);
                                Log.d("AsyncTask", "break true");
                            }
                            inner_state++;
                            break;
                        default:
                            if (inner_state < 7) {
                                inner_state++;
                                break;
                            }
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;
                case STATE_CHARGING:
                    switch (inner_state) {
                        case 0:
                            inner_state++;
                            break;
                        case 1:
                            inner_state++;
                            break;
                        case 2:
                            inner_state++;
                            break;
                        case 3:
                            inner_state++;
                            break;
                        case 4:
                            inner_state++;
                            break;
                        case 5:
                            Log.d("AsyncTask", "charging value: " + input);
                            if(input > 7) {
                                Logic.instance.charging = true;
                                Log.d("AsyncTask", "Charging true");
                            } else {
                                Logic.instance.charging = false;
                                Log.d("AsyncTask", "Charging false");
                            }
                            inner_state++;
                            break;
                        default:
                            if (inner_state < 7) {
                                inner_state++;
                                break;
                            }
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;


                default:
                    Log.d("Fuelshare StateMachine", "OUTER DEFAULT");
                    outer_state = STATE_INIT;
                    break;
            }
        }
    }



