package com.example.anders.fuelshare.data;

import android.util.Log;

/**
 * Created by anders on 25-11-2015.
 *
 * This is the class which handles all the backend logic,
 * it is designed as a singleton so all threads will access the same data.
 */
public class Logic {
    public static Logic instance = new Logic();
    private boolean charging;
    private int[] distance;
    private int[] battery;
    private int velocity;

    private Logic() {
        startData(); //testData();
    }

    private void startData(){
        distance = new int[] {};
        battery = new int[] {};
        velocity = 0;
    }

    private void testData(){
        distance = new int[] {21000, 24000};
        battery = new int[] {211, 187, 50, 120};
        /* Change the battery and distance from int[] to own classes, containing int and datestamp*/
    }

    /**
     * set the distance
     * Adds dist to the array of distances.
     * @param dist - lastest distance.
     */
    public void setDistance(int dist) {
        int[] tempList = new int[this.distance.length+1];
        for (int i = 0; i < this.distance.length; i++) {
            tempList[i] = this.distance[i];
        }
        tempList[tempList.length - 1] = dist;
        this.distance = tempList;
        Log.i("Fuelshare logic", "Distance stored: "+dist);
    }

    /**
     * get distance
     * @return - the last distance in the distance array.
     */
    public int getDistance() {
     return this.distance[this.distance.length-1];
    }

    /**
     * set the battery
     * @param bat - the unconverted battery level
     */
    public void setBattery(int bat) {
        int[] tempList = new int[battery.length+1];
        for (int i = 0; i < battery.length; i++) {
            tempList[i] = battery[i];
        }
        tempList[tempList.length - 1] = bat;
        battery = tempList;
        Log.i("Fuelshare logic", "battery stored: "+bat);
    }

    /**
     * get battery level
     * @return - The last battery level in unconverted int.
     */
    public int getBattery() {
        return battery[battery.length-1];
    }

    /**
     * get battery level procent
     * @return - The last battery level converted to procent.
     */
    public int getBatteryProcent() {
        return (battery[battery.length-1]/2)-5;
    }

    public int getRemainingDistance(){
        return 0;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity - Constants.VELOCITY_NULLER;
    }

    public int getVelocity() {
        return velocity;
    }
    public boolean isCharging() {
        return charging;
    }
    public void setCharging(boolean charging) {
        this.charging = charging;
    }
}
