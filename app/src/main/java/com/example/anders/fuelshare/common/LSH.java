package com.example.anders.fuelshare.common;

import java.util.ArrayList;
import java.util.List;

/**
 * LocalStorageHandler - singleton
 * Created by anders on 28-10-2015.
 */
public class LSH {
    private static LSH ourInstance = new LSH();
    private static List<Double> batteryLevel = new ArrayList<Double>();
    private static double distance = 0;

    public synchronized static LSH getInstance() {
        return ourInstance;
    }

    private LSH() {
        distance = 0;
        batteryLevel = new ArrayList<Double>(0);
    }

    public synchronized void setBat(double bat) {
        batteryLevel.add(bat);
        System.out.println(bat);
    }
    public synchronized double getLastBat() {
        if(!batteryLevel.isEmpty()) {
            return batteryLevel.get(batteryLevel.size());
        }
        return 0;
    }

    public synchronized void setDist(double dist) {
        System.out.println(dist);
        distance = dist;
    }
    public synchronized double getDist(){
        return distance;
    }


}
