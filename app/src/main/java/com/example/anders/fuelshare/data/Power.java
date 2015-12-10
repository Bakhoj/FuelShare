package com.example.anders.fuelshare.data;

import java.util.Date;

/**
 * Created by anders on 02-12-2015.
 */
public class Power {
    public int powerLevel;
    public Date date;

    public Power(int powerLevel){
        this.powerLevel = powerLevel;
        this.date = new Date();
    }
}
