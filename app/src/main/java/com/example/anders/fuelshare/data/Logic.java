package com.example.anders.fuelshare.data;

import android.util.Log;

import java.util.Date;

/**
 * Created by anders on 25-11-2015.
 *
 * This is the class which handles all the backend logic,
 * it is designed as a singleton so all threads will access the same data.
 */
public class Logic {
    public static Logic instance = new Logic();
    private Distance[] distance;
    private Power[] battery;
    public boolean charging;
    private int velocity, brakeCounter;
    private boolean turnedOn, brakePedal, continuousBraking;
    public boolean asyncRunning;
    public AsyncBluetooth asyncBluetooth;

    private Logic() {
        startData();
        //testData();
    }

    private void startData() {
        distance = new Distance[] {};
        battery = new Power[] {};
        velocity = 0;
        brakePedal = false;
        charging = false;
        turnedOn = false;
        asyncRunning = false;
//        asyncBluetooth = new AsyncBluetooth();
    }

    private void testData() {
        distance = new Distance[] {new Distance(23000)};
        battery = new Power[] {new Power(211), new Power(120)};
        velocity = 0;
        brakePedal = false;
        charging = false;
        turnedOn = false;
        asyncRunning = false;
    }

    /**
     * set the distance
     * Adds dist to the array of distances.
     * @param dist - lastest distance.
     */
    public void setDistance(int dist) {
        if(distance.length > 0) {
            if (dist == distance[distance.length - 1].dist) {
                Log.i("Fuelshare logic", "Distance was same: " + dist);
                return;
            }
        }
        Distance[] tempList = new Distance[distance.length+1];
        for (int i = 0; i < distance.length; i++) {
            tempList[i] = distance[i];
        }
        tempList[tempList.length - 1] = new Distance(dist);
        distance = tempList;
        Log.i("Fuelshare logic", "Distance stored: "+dist);
    }

    /**
     * get distance
     * @return - the last distance in the distance array.
     */
    public int getDistance() {
        if(distance.length < 1) { return 0; }
     return distance[distance.length-1].dist;
    }

    /**
     * set the battery
     * @param bat - the unconverted battery level
     */
    public void setBattery(int bat) {
        if(battery.length > 0) {
            if (bat == battery[battery.length - 1].powerLevel) {
                Log.i("Fuelshare logic", "Battery level was same: " + bat);
                return;
            }
        }
        Power[] tempList = new Power[battery.length+1];
        for (int i = 0; i < battery.length; i++) {
            tempList[i] = battery[i];
        }
        tempList[tempList.length - 1] = new Power(bat);
        battery = tempList;
        Log.i("Fuelshare logic", "Battery level stored: "+bat);
    }

    /**
     * get battery level
     * @return - The last battery level in unconverted int.
     */
    public int getBattery() {
        return battery[battery.length-1].powerLevel;
    }

    /**
     * get battery level procent
     * @return - The last battery level converted to procent.
     */
    public double getBatteryProcent() {
        if(battery.length < 1) { return 0;}
        return (battery[battery.length-1].powerLevel/2.0)-5.0;
    }

    public int getRemainingDistance(){
        return 0;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity - 65024;
        if (this.velocity == 511) {
            this.velocity = 0;
            turnedOn = false;
        } else {
            turnedOn = true;
        }
    }

    public int getVelocity() {
        return velocity;
    }

    public boolean isTurnedOn() {
        return turnedOn;
    }

    public boolean isBrakePedal() {
        return brakePedal;
    }

    public void setBrakePedal(boolean brakePedal) {
        if(continuousBraking && brakePedal) {
            this.brakePedal = brakePedal;
            return;
        }
        if (brakePedal && !continuousBraking) {
            this.brakePedal = brakePedal;
            this.continuousBraking = true;
            brakeCounter++;
            return;
        }
        this.brakePedal = brakePedal;
        this.continuousBraking = false;
    }

    public int getBrakeCounter() {
        return brakeCounter;
    }
}

