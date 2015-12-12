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
    private Distance[] distance;
    private Power[] battery;
    public boolean breakPedal;
    private int velocity;
    public boolean charging;


    private Logic() {
        //startData();
        testData();
    }

    private void startData(){
        distance = new Distance[] {};
        battery = new Power[] {};
        velocity = 65024;
        breakPedal = false;
    }

    private void testData(){
        distance = new Distance[] {new Distance(23000)};
        battery = new Power[] {new Power(211), new Power(200)};
        velocity = 65024;
        breakPedal = false;
        charging = false;
    }

    /**
     * set the distance
     * Adds dist to the array of distances.
     * @param dist - lastest distance.
     */
    public void setDistance(int dist) {
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
        if(distance.length == 0) { return 0; }
     return distance[distance.length-1].dist;
    }

    /**
     * set the battery
     * @param bat - the unconverted battery level
     */
    public void setBattery(int bat) {
        Power[] tempList = new Power[battery.length+1];
        for (int i = 0; i < battery.length; i++) {
            tempList[i] = battery[i];
        }
        tempList[tempList.length - 1] = new Power(bat);
        Log.i("Fuelshare logic", "battery stored: "+bat);
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
    public int getBatteryProcent() {
        if(battery.length == 0) { return 0;}
        return (battery[battery.length-1].powerLevel/2)-5;
    }

    public int getRemainingDistance(){
        return 0;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity - 65024;
    }

    public int getVelocity() {
        return velocity;
    }
}
