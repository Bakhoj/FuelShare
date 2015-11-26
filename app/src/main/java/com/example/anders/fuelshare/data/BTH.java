package com.example.anders.fuelshare.data;

import android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;

import com.example.anders.fuelshare.common.LSH;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * BlueTooth Handler
 * Created by anders on 28-10-2015.
 *
 * Handles and keeps the bluetooth connection for the whole application
 * should always call BTH.getInstance() before interacting with BTH
 */
public class BTH {

    BluetoothAdapter mBluetoothAdapter = null;

    private static BTH ourInstance = new BTH();
    private static Handler mHandler;

    /**
     * getInstance()
     * when you want to interact with the BluetoothHandler you get the instance
     * with this method.
     * @return ourInstance
     */
    public static BTH getInstance(){ return ourInstance;}
    private BTH(){
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

    public void testMethod(BluetoothDevice tmDevice){
        ConnectThread ct = new ConnectThread(tmDevice);
        new Thread(ct).start();

    }

    /** ########################################################################################
     *                      PRIVATE ConnectThread
     *  ########################################################################################
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//        private final UUID MY_UUID = UUID.fromString("0x0003");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;

            try{
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch(IOException e){ }
            mmSocket = tmp;
        }
        public void run() {
            mBluetoothAdapter.cancelDiscovery();

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
            ConnectedThread ct = new ConnectedThread(mmSocket);
            new Thread(ct).start();
            //manageConnectedSocket(mmSocket);
        }

        public BluetoothSocket getSocket(){
            return mmSocket;
        }
        public BluetoothDevice getDevice(){
            return mmDevice;
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
    /** ########################################################################################
     *                      PRIVATE ConnectedThread
     *  ########################################################################################
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final DataInputStream dInStream;
//        private final OutputStream mmOutStream;
        private LSH lsh;
        private int distance;


        private int outer_state;
        private int inner_state;
        private int length;
        private int state;
        private final int STATE_INIT = 0;
        private final int STATE_CHARGE = 374;   //maybe 884 or 3 and 116
        private final int STATE_ODOMETER = 412; //maybe 1042 or 4 and 18

/*      private final int STATE_CHARGE_1 = 1;
        private final int STATE_CHARGE_2 = 2;
        private final int STATE_CHARGE_3 = 3;
        private final int STATE_CHARGE_4 = 4;
//        private final int STATE_CHARGE_5 = 5;
//        private final int STATE_CHARGE_6 = 6;
//        private final int STATE_CHARGE_7 = 7;
//        private final int STATE_CHARGE_8 = 8;
//        private final int STATE_CHARGE_9 = 9;
//        private final int STATE_CHARGE_10 = 10;
//        private final int STATE_CHARGE_11 = 11;

        private final int STATE_ODOMETER_1 = 12;
        private final int STATE_ODOMETER_2 = 13;
        private final int STATE_ODOMETER_3 = 14;
        private final int STATE_ODOMETER_4 = 15;
        private final int STATE_ODOMETER_5 = 16;
        private final int STATE_ODOMETER_6 = 17;
        private final int STATE_ODOMETER_7 = 18;
*/
        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//            DataInputStream tmpDin = null;
            lsh = LSH.getInstance();
            state = STATE_INIT;

            try {
                System.out.println("Making Inputstream ...");
                tmpIn = socket.getInputStream();
                System.out.println("Done");
//                System.out.println("Making Outputstream ...");
//                tmpOut = socket.getOutputStream();
//                System.out.println("Done");
//                tmpDin = new DataInputStream(tmpIn);
            } catch(IOException e) {
                System.out.println("Failed making streams");}

            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//            dInStream = tmpDin;
            dInStream = null;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
//                    for(int i = 0; i< thing.length;i++) {
//                        bytes = dInStream.readUnsignedByte();
//                    bytes = dInStream.readUnsignedShort();
                        bytes = mmInStream.read(buffer);
                        System.out.println("READING UNSIGNED: " + bytes);
                        stateMachine(bytes);
                        //System.out.println("READING UNSIGNED: " + bytes);
//                        mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
//                                .sendToTarget();
//                    }
                } catch (IOException e) {
                    System.out.println("Failed reading from inputstream");
                    break;
                }
            }
        }

        private void stateMachine(int input) {
            switch(outer_state) {
                case STATE_INIT:
                    inner_state = 0;
                    outer_state = input;

                    /* måske lave ovenstående om, måske få den til at kalde på
                    * stateMachine() igen og tilføje inner state check til først ID.
                    * */
                    break;
                case STATE_CHARGE:
                    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
                            + "\nSTATE_CHARGE\n"
                            + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
                            );
                    switch(inner_state) {
                        case 0:
                            inner_state++;
                            break;
                        case 1:
                            lsh.setBat(input);
                            Logic.instance.setBattery(input);
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
                            inner_state++;
                            break;
                        case 6:
                            inner_state++;
                            break;
                        case 7:
                            inner_state++;
                            break;
                        default:
                            System.out.println("INNER DEFAULT ... WTF?");
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;

                case STATE_ODOMETER:
                    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
                                    + "\nSTATE_ODOMETER\n"
                                    + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
                    );
                    switch(inner_state) {
                        case 0:
                            //VELOCITY 1 of 2
                            inner_state++;
                            break;
                        case 1:
                            //VELOCITY 2 of 2
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
                            lsh.setDist(distance);
                            Logic.instance.setDistance(distance);
                            inner_state++;
                            break;
                        case 5:
                            inner_state++;
                            break;
                        case 6:
                            inner_state++;
                            break;
                        case 7:
                            inner_state++;
                            break;
                        default:
                            System.out.println("INNER DEFAULT ... WTF?");
                            outer_state = STATE_INIT;
                            break;
                    }
                    break;

                default:
                    System.out.println("OUTER DEFAULT");
                    outer_state = STATE_INIT;
                    break;
            }
        }

        /*
        private void stateMacine(int buff) {
            //System.out.println(buff);
            //lsh = LSH.getInstance();
            switch(state) {
                case STATE_INIT:
                    if(buff == 3){
                        state = STATE_CHARGE_1;
                        System.out.println("STATE_NONE");
                        break;
                    }
                    if(buff == 4){
                        state = STATE_ODOMETER_1;
                        System.out.println("STATE_NONE");
                        break;
                    }
                    break;
                case STATE_CHARGE_1:
                    if(buff == 116){ state = STATE_CHARGE_2;}
                    else {state = STATE_INIT; }
                    System.out.println("STATE_CHARGE_1");
                    break;
                case STATE_CHARGE_2:
                    if(buff == 8){ state = STATE_CHARGE_3;}
                    else {state = STATE_INIT; }
                    System.out.println("STATE_CHARGE_2");
                    break;
                case STATE_CHARGE_3:
                    if(buff == 10){ state = STATE_CHARGE_4;}
                    else {state = STATE_INIT; }
                    System.out.println("STATE_CHARGE_3");
                    break;
                case STATE_CHARGE_4:
                    lsh.setBat(buff);
                    state  = STATE_INIT;
                    System.out.println("STATE_CHARGE_4");
                    break;
                case STATE_ODOMETER_1:
                    if(buff == 18){ state = STATE_ODOMETER_2;}
                    else {state = STATE_INIT; }
                    System.out.println("STATE_ODOMETER_1");
                    break;
                case STATE_ODOMETER_2:
                    if(buff == 8){ state = STATE_ODOMETER_3;}
                    else {state = STATE_INIT; }
                    System.out.println("STATE_ODOMETER_2");
                    break;
                case STATE_ODOMETER_3:
                    //VELOCITY 1 of 2
                    state = STATE_ODOMETER_4;
                    System.out.println("STATE_ODOMETER_3");
                    break;
                case STATE_ODOMETER_4:
                    //VELOCITY 2 of 2
                    state = STATE_ODOMETER_5;
                    System.out.println("STATE_ODOMETER_4");
                    break;
                case STATE_ODOMETER_5:
                    //DISTANCE 1 of 3
                    distance = buff <<16;
                    state = STATE_ODOMETER_6;
                    System.out.println("STATE_ODOMETER_5");
                    break;
                case STATE_ODOMETER_6:
                    //DISTANCE 2 of 3
                    distance += buff <<8;
                    state = STATE_ODOMETER_7;
                    System.out.println("STATE_ODOMETER_6");
                    break;
                case STATE_ODOMETER_7:
                    distance += buff;
                    lsh.setDist(distance);
                    //DISTANCE 3 of 3
                    state = STATE_INIT;
                    System.out.println("STATE_ODOMETER_7");
                    break;
                default:
                    System.out.println("Default state ... wtf?");
                    break;
            }
        } */

/*        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {}
        }
*/
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

}