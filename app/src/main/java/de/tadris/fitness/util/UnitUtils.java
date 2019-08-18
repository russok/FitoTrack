/*
 * Copyright (c) 2019 Jannis Scheibe <jannis@tadris.de>
 *
 * This file is part of FitoTrack
 *
 * FitoTrack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FitoTrack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tadris.fitness.util;

public class UnitUtils {

    public static String getHourMinuteTime(long time){
        long seks= time / 1000;
        long mins= seks / 60;
        int hours= (int)mins / 60;
        int remainingMinutes= (int)mins % 60;

        if(hours > 0){
            return hours + " h " + remainingMinutes + " m";
        }else{
            return remainingMinutes + " min";
        }
    }

    public static String getHourMinuteSecondTime(long time){
        long totalSeks= time / 1000;
        long totalMins= totalSeks / 60;
        long hours= totalMins / 60;
        long mins= totalMins % 60;
        long seks= totalSeks % 60;
        String minStr= (mins < 10 ? "0" : "") + mins;
        String sekStr= (seks < 10 ? "0" : "") + seks;
        return hours + ":" + minStr + ":" + sekStr;
    }

    /**
     *
     * @param pace Pace in min/km
     * @return Pace
     */
    public static String getPace(double pace){
        // TODO: use preferred unit chosen by user
        return round(pace, 1) + " min/km";
    }

    /**
     *
     * @param consumption consumption in kcal/km
     * @return
     */
    public static String getRelativeEnergyConsumption(double consumption){
        // TODO: use preferred unit chosen by user
        return round(consumption, 2) + " kcal/km";
    }

    /**
     *
     * @param distance Distance in meters
     * @return String in preferred unit
     */
    public static String getDistance(int distance){
        // TODO: use preferred unit chosen by user
        if(distance >= 1000){
            return getDistanceInKilometers((double)distance);
        }else{
            return getDistanceInMeters(distance);
        }
    }

    /**
     *
     * @param speed speed in m/s
     * @return speed in km/h
     */
    public static String getSpeed(double speed){
        // TODO: use preferred unit chosen by user
        return round(speed*3.6, 1) + " km/h";
    }

    public static String getDistanceInMeters(int distance){
        return distance + " m";
    }

    public static String getDistanceInKilometers(double distance){
        return round(distance / 1000, 1) + " km";
    }

    public static double round(double d, int count){
        return (double)Math.round(d * Math.pow(10, count)) / Math.pow(10, count);
    }


}
