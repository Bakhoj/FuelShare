package com.example.anders.fuelshare.common;

/**
 * LocalStorageHandler - singleton
 * Created by anders on 28-10-2015.
 */
public class LSH {
    private static LSH ourInstance = new LSH();

    public static LSH getInstance() {
        return ourInstance;
    }

    private LSH() {
    }
}
