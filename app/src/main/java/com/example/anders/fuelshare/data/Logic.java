package com.example.anders.fuelshare.data;

/**
 * Created by anders on 25-11-2015.
 *
 * This is the class which handles all the backend logic,
 * it is designed as a singleton so all threads will access the same data.
 */
public class Logic {
    public static Logic instance = new Logic();
    private int[] distance;
    private int[] battery;


    private Logic() {
        testData();
    }

    private void testData(){
        distance = new int[] {21000, 24000};
        battery = new int[] {211, 187, 50, 120};
    }

    /**
     * set the distance
     * Adds dist to the array of distances.
     * @param dist - lastest distance.
     */
    public void setDistance(int dist) {
        int[] tempList = new int[distance.length+1];
        for (int i = 0; i < distance.length; i++) {
            tempList[i] = distance[i];
        }
        tempList[tempList.length - 1] = dist;
        distance = tempList;
    }

    /**
     * get distance
     * @return - the last distance in the distance array.
     */
    public int getDistance() {
     return distance[distance.length-1];
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
}
