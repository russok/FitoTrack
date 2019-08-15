package de.tadris.fitness.util;

import android.content.Context;

public class UnitUtils {

    public static String getHourMinuteTime(long time){
        long seks= time / 1000;
        long mins= seks / 60;
        int hours= (int)mins / 60;
        int remainingMinutes= (int)mins % 60;

        if(hours > 0){
            return hours + "h " + remainingMinutes + "m";
        }else{
            return remainingMinutes + "min";
        }
    }

}
