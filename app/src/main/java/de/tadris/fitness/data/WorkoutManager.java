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

package de.tadris.fitness.data;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.util.CalorieCalculator;

public class WorkoutManager {

    public static void insertWorkout(Context context, Workout workout, List<WorkoutSample> samples){
        AppDatabase db= Instance.getInstance(context).db;


        workout.id= System.currentTimeMillis();

        // Delete Samples with same time
        for(int i= samples.size()-2; i >= 0; i--){
            WorkoutSample sample= samples.get(i);
            WorkoutSample lastSample= samples.get(i+1);
            if(sample.absoluteTime == lastSample.absoluteTime){
                samples.remove(lastSample);
                Log.i("WorkoutManager", "Removed samples at " + sample.absoluteTime + " rel: " + sample.relativeTime + "; " + lastSample.relativeTime);
            }
        }

        // Calculating values
        double length= 0;
        for(int i= 1; i < samples.size(); i++){
            double sampleLength= samples.get(i - 1).toLatLong().sphericalDistance(samples.get(i).toLatLong());
            long timeDiff= (samples.get(i).relativeTime - samples.get(i - 1).relativeTime) / 1000;
            length+= sampleLength;
            samples.get(i).speed= Math.abs(sampleLength / timeDiff);
        }
        workout.length= (int)length;
        workout.avgSpeed= ((double) workout.length) / ((double) workout.duration / 1000);
        workout.avgPace= ((double)workout.duration / 1000 / 60) / ((double) workout.length / 1000);
        workout.calorie= CalorieCalculator.calculateCalories(workout, Instance.getInstance(context).userPreferences.getUserWeight());

        // Setting workoutId in the samples
        int i= 0;
        double topSpeed= 0;
        double elevationSum= 0; // Sum of elevation
        double pressureSum= 0; // Sum of elevation
        for(WorkoutSample sample : samples){
            i++;
            sample.id= workout.id + i;
            sample.workoutId= workout.id;
            elevationSum+= sample.elevation;
            pressureSum+= sample.tmpPressure;
            if(sample.speed > topSpeed){
                topSpeed= sample.speed;
            }
        }

        workout.topSpeed= topSpeed;

        // Calculating height data
        boolean pressureDataAvailable= samples.get(0).tmpPressure != -1;
        double avgElevation= elevationSum / samples.size();
        double avgPressure=  pressureSum  / samples.size();

        workout.ascent = 0;
        workout.descent = 0;

        for(i= 0; i < samples.size(); i++){
            WorkoutSample sample= samples.get(i);

            if(pressureDataAvailable){
                // Altitude Difference to Average Elevation in meters
                float altitude_difference =
                        SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, sample.tmpPressure) -
                                SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, (float) avgPressure);
                sample.elevation= avgElevation + altitude_difference;
            } // Else: use already set GPS elevation in WorkoutSample.elevation

            if(i >= 1){
                WorkoutSample lastSample= samples.get(i-1);
                double diff= sample.elevation - lastSample.elevation;
                if(diff > 0){
                    workout.ascent += diff;
                }else{
                    workout.descent += Math.abs(diff);
                }
            }
        }


        // Saving workout and samples
        db.workoutDao().insertWorkoutAndSamples(workout, samples.toArray(new WorkoutSample[0]));

    }

    public static void roundSpeedValues(List<WorkoutSample> samples){
        for(int i= 0; i < samples.size(); i++){
            WorkoutSample sample= samples.get(i);
            if(i == 0){
                sample.tmpRoundedSpeed= (sample.speed+samples.get(i+1).speed) / 2;
            }else if(i == samples.size()-1){
                sample.tmpRoundedSpeed= (sample.speed+samples.get(i-1).speed) / 2;
            }else{
                sample.tmpRoundedSpeed= (sample.speed+samples.get(i-1).speed+samples.get(i+1).speed) / 3;
            }
        }
    }

    public static void calculateInclination(List<WorkoutSample> samples){
        samples.get(0).tmpInclination= 0;
        for(int i= 1; i < samples.size(); i++){
            WorkoutSample sample= samples.get(i);
            WorkoutSample lastSample= samples.get(i);
            double elevationDifference= sample.elevation - sample.elevation;
            double distance= sample.toLatLong().sphericalDistance(lastSample.toLatLong());
            sample.tmpInclination= (float)(elevationDifference*100/distance);
        }
    }

}
