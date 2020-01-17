/*
 * Copyright (c) 2020 Jannis Scheibe <jannis@tadris.de>
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

package de.tadris.fitness.util.unit;

import android.content.Context;
import android.preference.PreferenceManager;

public class UnitUtils {

    private static final Unit UNITS_METRIC = new Metric();
    private static final Unit UNITS_METRIC_PHYSICAL = new MetricPhysical();
    private static final Unit UNITS_IMPERIAL_YARDS = new Imperial();
    private static final Unit UNITS_IMPERIAL_METERS = new ImperialWithMeters();
    private static final Unit[] supportedUnits = new Unit[]{
            UNITS_METRIC, UNITS_METRIC_PHYSICAL, UNITS_IMPERIAL_YARDS, UNITS_IMPERIAL_METERS
    };

    public static Unit CHOSEN_SYSTEM= UNITS_METRIC;

    public static void setUnit(Context context){
        String id = PreferenceManager.getDefaultSharedPreferences(context).getString("unitSystem", String.valueOf(UnitUtils.UNITS_METRIC.getId()));
        assert id != null;
        setUnit(Integer.parseInt(id));
    }

    private static void setUnit(int id) {
        CHOSEN_SYSTEM= UNITS_METRIC;
        for(Unit unit : supportedUnits){
            if(id == unit.getId()){
                CHOSEN_SYSTEM= unit;
            }
        }
    }

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
        long totalSecs = time / 1000;
        long totalMins = totalSecs / 60;
        long hours= totalMins / 60;
        long mins= totalMins % 60;
        long secs = totalSecs % 60;
        String minStr= (mins < 10 ? "0" : "") + mins;
        String sekStr = (secs < 10 ? "0" : "") + secs;
        return hours + ":" + minStr + ":" + sekStr;
    }

    public static String getPace(double metricPace) {
        double one= CHOSEN_SYSTEM.getDistanceFromKilometers(1);
        double secondsTotal = 60 * metricPace / one;
        int minutes = (int) secondsTotal / 60;
        int seconds = (int) secondsTotal % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds + " min/" + CHOSEN_SYSTEM.getLongDistanceUnit();
    }

    /**
     *CHOSEN_SYSTEM.getLongDistanceUnit()
     * @param consumption consumption in kcal/km
     */
    public static String getRelativeEnergyConsumption(double consumption){
        double one= CHOSEN_SYSTEM.getDistanceFromKilometers(1);
        return round(consumption / one, 2) + " kcal/" + CHOSEN_SYSTEM.getLongDistanceUnit();
    }

    /**
     *
     * @param distance Distance in meters
     * @return String in preferred unit
     */
    public static String getDistance(int distance){
        double units= CHOSEN_SYSTEM.getDistanceFromMeters(distance);
        if(units >= 1000){
            return round(units / 1000, 1) + " " + CHOSEN_SYSTEM.getLongDistanceUnit();
        }else{
            return (int)units + " " + CHOSEN_SYSTEM.getShortDistanceUnit();
        }
    }

    /**
     *
     * @param speed speed in m/s
     * @return speed in km/h
     */
    public static String getSpeed(double speed){
        return round(CHOSEN_SYSTEM.getSpeedFromMeterPerSecond(speed), 1) + " " + CHOSEN_SYSTEM.getSpeedUnit();
    }

    private static double round(double d, int count) {
        return (double)Math.round(d * Math.pow(10, count)) / Math.pow(10, count);
    }


}
