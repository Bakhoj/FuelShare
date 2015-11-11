package com.example.anders.fuelshare.data;

import android.bluetooth.BluetoothAdapter;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;

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
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            DataInputStream tmpDin = null;

            try {
                System.out.println("Making Inputstream ...");
                tmpIn = socket.getInputStream();
                System.out.println("Done");
                System.out.println("Making Outputstream ...");
                tmpOut = socket.getOutputStream();
                System.out.println("Done");
                tmpDin = new DataInputStream(tmpIn);
            } catch(IOException e) {
                System.out.println("Failed making streams");}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            dInStream = tmpDin;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;


            while(true){
                try {
//                    bytes = mmInStream.read(buffer);
//                    System.out.println(bytes);
//                    dInStream.readFully(buffer, 0, 4);
//                    bytes = dInStream.readByte();
//                    System.out.println(buffer.toString());
//                    dInStream.readFully(readByte);
                    bytes = dInStream.readUnsignedByte();
//                    System.out.println("READING THIS: " + readByte[0]);
                    System.out.println("READING UNSIGNED: " + bytes);
//                    int i = 97 << 8;
//                    System.out.println("number: " + i);
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
//                    System.out.println("Handler says: " + mHandler.obtainMessage());
                } catch (IOException e) {
                    System.out.println("Failed reading from inputstream");
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {}
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
}