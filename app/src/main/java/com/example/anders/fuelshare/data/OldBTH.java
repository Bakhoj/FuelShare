package com.example.anders.fuelshare.data;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

/**
 * BlueTooth Handler
 * Created by anders on 28-10-2015.
 *
 * Handles and keeps the bluetooth connection for the whole application
 * should always call BTH.getInstance() before interacting with BTH
 */
public class OldBTH {

    BluetoothAdapter mBluetoothAdapter = null;
    AsyncBluetooth ab = new AsyncBluetooth();

    private static OldBTH ourInstance = new OldBTH();
    private static Handler mHandler;
    private static TheRunner theThread;

    /**
     * getInstance()
     * when you want to interact with the BluetoothHandler you get the instance
     * with this method.
     * @return ourInstance
     */
    public static OldBTH getInstance(){ return ourInstance;}
    public static Thread getTheThread() { return theThread;}

    public void runAsync() {
        ab.execute();
    }

    private OldBTH(){
        //ab.execute();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            System.out.println("System doesn't support Bluetooth");
            return;
        }
        mHandler = new Handler();
    }

    /**
     * connectBT
     *
     * @return Set<String> which contains all the paired devices. NULL if none.
     */
    public BluetoothDevice connectBT() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        if(pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                //Set<String> mArrayAdapter = null;
                System.out.println(device.getName() + "\n" + device.getAddress());
                //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                return device;
            }
            //return pairedDevices;
        }
        return null;
    }

    /**
     * discoverDevices (not in use)
     *
     * Should shearch for all visable bluetooth devices.
     */
    public void discoverDevices(){

    }

    public boolean startBt(Activity a){
        if(!isBtEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            a.startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
        }
        return true;
    }
    public boolean isBtEnabled(){
        return mBluetoothAdapter.isEnabled();
    }
    public boolean checkConnection(){
        return true;
    }
    public Handler getHandler(){
        return mHandler;
    }

    private ConnectThread ct;

    public void testMethod(BluetoothDevice tmDevice){
        ct = new ConnectThread(tmDevice);
        new Thread(ct).start();
        /*if(theThread == null ){
            theThread = new TheRunner();
        }
        new Thread(theThread).start();*/
    }

    public void close(){
        ct.cancel();
        theThread.cancel();
    }

    /** ########################################################################################
     *                      PRIVATE TheRunner
     *  ########################################################################################
     */
    private class TheRunner extends Thread {
        ConnectThread cttr;
        Boolean keepRunning;
        OldBTH bth;
        public TheRunner(){
            bth = OldBTH.getInstance();
            BluetoothDevice tmDevice = bth.connectBT();
            cttr = new ConnectThread(tmDevice);
            keepRunning = true;
        }

        @Override
        public void run() {
            BluetoothDevice tmDevice = OldBTH.getInstance().connectBT();
            cttr = new ConnectThread(tmDevice);
            new Thread(cttr).start();

            while(keepRunning){
                if(!cttr.getConnectedThread().isAlive()) {
                    tmDevice = OldBTH.getInstance().connectBT();
                    cttr = new ConnectThread(tmDevice);
                    new Thread(cttr).start();
                }
            }

        }

        public void cancel(){
            cttr.cancel();
            keepRunning = false;
        }
    }

    /** ########################################################################################
     *                      PRIVATE ConnectThread
     *  ########################################################################################
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private boolean keepRunning;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//        private final UUID MY_UUID = UUID.fromString("0x0003");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            keepRunning = true;
            ParcelUuid list[] = device.getUuids();
            Log.d("UUID bluetooth", " " + list[0]);
            try{
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch(IOException e){ }
            mmSocket = tmp;
        }
        ConnectedThread ct;

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            keepRunning = true;

            try {
                mmSocket.connect();
                System.out.println("Succes, socket is connected");
            } catch (IOException connectException) {
                try {
                    System.out.println("Failed to connect socket, trying to close socket ...");
                    mmSocket.close();
                } catch(IOException closeException) {
                    System.out.println("Failed to close connection socket.");
                }
            }

            ct = new ConnectedThread(mmSocket);
            new Thread(ct).start();
            //manageConnectedSocket(mmSocket);

            while (keepRunning) {
                if(ct.isAlive()){
                    Log.d("The thread thing", "true");
                } else {
                    Log.d("The thread thing", "false");
                }
                keepRunning = false;
//                if(!ct.isAlive()){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        mmSocket.connect();
                        System.out.println("Succes, socket is connected");
                        ct = new ConnectedThread(mmSocket);
                        new Thread(ct).start();
                    } catch (IOException connectException) {
                        try {
                            System.out.println("Failed to connect socket, trying to close socket ...");
                            mmSocket.close();
                        } catch(IOException closeException) {
                            System.out.println("Failed to close connection socket.");
                        }
                    }
//                }
            }
        }

        public ConnectedThread getConnectedThread(){
            if(ct != null) {
                return ct;
            }
            return null;
        }

        public BluetoothSocket getSocket(){
            return mmSocket;
        }
        public BluetoothDevice getDevice(){
            return mmDevice;
        }

        public void cancel() {
            try {
                keepRunning = false;
                ct.cancel();
                System.out.println("Closing bluetooth socket ...");
                mmSocket.close();
                System.out.println("Bluetooth socket has been closed");
            } catch(IOException e) {
                System.out.println("failed to close bluetooth socket: " + e);
            }
        }
    }
    /** ########################################################################################
     *                      PRIVATE ConnectedThread
     *  ########################################################################################
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
//        private final InputStream mmInStream;
        private final DataInputStream dInStream;

        private int distance, velocity, breakRead;
        private boolean breakPedal;


        private int outer_state, inner_state;
        private final int STATE_INIT = 0;
        private final int STATE_CHARGE = 884;       // 0x374 (884)
        private final int STATE_ODOMETER = 1042;    // 0x412 (1042)
        private final int STATE_BREAKPEDAL = 520;      // 0x208 (520)

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
            DataInputStream tmpDin = null;

            try {
                System.out.println("Making Inputstream ...");
                tmpIn = socket.getInputStream();
                System.out.println("Done");
//                System.out.println("Making Outputstream ...");
//                tmpOut = socket.getOutputStream();
//                System.out.println("Done");
                tmpDin = new DataInputStream(tmpIn);
            } catch(IOException e) {
                System.out.println("Failed making streams");}

//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
            dInStream = tmpDin;
//            dInStream = null;
        }

        public void run() {
            try {

                String input = dInStream.readLine();
                    if(input.contains("ID:") && input.contains("Data:")) {
                        outer_state = STATE_INIT;
                        String[] first = input.split("  Data: ");
                        String id = first[0].replace("ID:", "");
                        id = id.replace(" ", "");
                        id = id.replace("\n", "");
                        String[] data = first[1].split(" ");
                        try {
                            stateMachine(Integer.parseInt(id, 16));
                        } catch (NumberFormatException e){ }
                        for(String s : data){
                            String st = s.replace(" ", "");
                            String str = st.replace("\n", "");
                            try {
                                stateMachine(Integer.parseInt(str, 16));
                            } catch (NumberFormatException e){ }
                        }
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void stateMachine(int input) {
            switch(outer_state) {
                case STATE_INIT:
                    inner_state = 0;
                    if(input == STATE_CHARGE || input == STATE_ODOMETER) {
                        outer_state = input;
                    }
                    break;
                case STATE_CHARGE:
                    switch(inner_state) {
                        case 0:
                            inner_state++;
                            break;
                        case 1:
                            Logic.instance.setBattery(input);
                            inner_state++;
                            break;
                        default:
                            if(inner_state < 7){
                                inner_state++;
                                break;
                            }
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;

                case STATE_ODOMETER:
                    switch(inner_state) {
                        case 0:
                            //VELOCITY 1 of 2
                            velocity = input<<8;
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
                            distance = input<<16;
                            inner_state++;
                            break;
                        case 3:
                            //DISTANCE 2 of 3
                            distance += input<<8;
                            inner_state++;
                            break;
                        case 4:
                            //DISTANCE 3 of 3
                            distance += input;
                            Logic.instance.setDistance(distance);
                            inner_state++;
                            break;
                        default:
                            if(inner_state < 7){
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
                            breakRead = input<<8;
                            inner_state++;
                            break;
                        case 3:
                            //BREAK 2 of 2
                            breakRead += input;
                            if(breakRead <= 24576){
                                breakPedal = false;
                            } else {
                                breakPedal = true;
                            }
                            inner_state++;
                            break;
                        default:
                            if(inner_state < 7){
                                inner_state++;
                                break;
                            }
                            outer_state = STATE_INIT;
                            break;
                    }


                default:
                    Log.d("Fuelshare StateMachine", "OUTER DEFAULT");
                    outer_state = STATE_INIT;
                    break;
            }
        }

        public void cancel() {
            try {
                System.out.println("Closing bluetooth socket ...");
                mmSocket.close();
                System.out.println("Bluetooth socket has been closed");
            } catch(IOException e) {
                System.out.println("failed to close bluetooth socket: " + e);
            }
        }
    }

    private class AsyncBluetooth extends AsyncTask<Void, Void, Void> {
        BluetoothAdapter mBluetoothAdapter = null;
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        private int distance, velocity, breakRead;
        private boolean breakPedal;

        private int outer_state, inner_state;
        private final int STATE_INIT = 0;
        private final int STATE_CHARGE = 884;       // 0x374 (884)
        private final int STATE_ODOMETER = 1042;    // 0x412 (1042)
        private final int STATE_BREAKPEDAL = 520;      // 0x208 (520)


        @Override
        protected void onPreExecute() {
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
                    if (device.getName().equals(Constants.DEVICE_NAME)) {
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

            while(!isCancelled()){
                try {
                    Log.d("AsyncTask", "Run readLine");
                    String input = mmInputstream.readLine();
                    Log.d("AsyncTask", "InputRead: " + input.toString());
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
                        publishProgress();
                    } else {
                        Log.d("AsyncTask", "Could not identify read input");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Log.d("AsyncTask", "ProgressUpdated!");
        }

        private void stateMachine(int input) {
            switch (outer_state) {
                case STATE_INIT:
                    inner_state = 0;
                    if (input == STATE_CHARGE || input == STATE_ODOMETER) {
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
                                breakPedal = false;
                            } else {
                                breakPedal = true;
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


                default:
                    Log.d("Fuelshare StateMachine", "OUTER DEFAULT");
                    outer_state = STATE_INIT;
                    break;
            }
        }
    }
}

