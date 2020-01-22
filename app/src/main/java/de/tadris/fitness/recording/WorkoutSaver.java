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

package de.tadris.fitness.recording;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;
import de.tadris.fitness.util.CalorieCalculator;

class WorkoutSaver {

    private final Context context;
    private final Workout workout;
    private final List<WorkoutSample> samples;
    private final AppDatabase db;

    public WorkoutSaver(Context context, Workout workout, List<WorkoutSample> samples) {
        this.context = context;
        this.workout = workout;
        this.samples = samples;
        db= Instance.getInstance(context).db;
    }

    public void saveWorkout(){
        setIds();
        clearSamplesWithSameTime();
        setSimpleValues();
        setTopSpeed();

        setRealElevation();
        setAscentAndDescent();

        setCalories();

        storeInDatabase();
    }

    private void setIds(){
        workout.id= System.currentTimeMillis();
        int i= 0;
        for(WorkoutSample sample : samples) {
            i++;
            sample.id = workout.id + i;
            sample.workoutId = workout.id;
        }
    }

    private void clearSamplesWithSameTime(){
        for(int i= samples.size()-2; i >= 0; i--){
            WorkoutSample sample= samples.get(i);
            WorkoutSample lastSample= samples.get(i+1);
            if(sample.absoluteTime == lastSample.absoluteTime){
                samples.remove(lastSample);
                Log.i("WorkoutManager", "Removed samples at " + sample.absoluteTime + " rel: " + sample.relativeTime + "; " + lastSample.relativeTime);
            }
        }
    }

    private void setSimpleValues(){
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
    }

    private void setTopSpeed(){
        double topSpeed= 0;
        for(WorkoutSample sample : samples){
            if(sample.speed > topSpeed){
                topSpeed= sample.speed;
            }
        }
        workout.topSpeed= topSpeed;
    }

    private void setRealElevation(){
        boolean pressureDataAvailable= samples.get(0).tmpPressure != -1;

        if(!pressureDataAvailable){
            // Because pressure data isn't available we just use the use GPS elevation
            // in WorkoutSample.elevation which was already set
            return;
        }

        double avgElevation= getAverageElevation();
        double avgPressure=  getAveragePressure();

        for(int i= 0; i < samples.size(); i++){
            WorkoutSample sample= samples.get(i);

            // Altitude Difference to Average Elevation in meters
            float altitude_difference =
                    SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, sample.tmpPressure) -
                            SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, (float) avgPressure);
            sample.elevation= avgElevation + altitude_difference;
        }
    }

    private double getAverageElevation(){
        return getAverageElevation(samples);
    }

    private double getAverageElevation(List<WorkoutSample> samples){
        double elevationSum= 0; // Sum of elevation
        for(WorkoutSample sample : samples){
            elevationSum+= sample.elevation;
        }

        return elevationSum / samples.size();
    }

    private double getAveragePressure(){
        double pressureSum= 0;
        for(WorkoutSample sample : samples){
            pressureSum+= sample.tmpPressure;
        }
        return pressureSum  / samples.size();
    }

    private void setAscentAndDescent(){
        workout.ascent = 0;
        workout.descent = 0;

        // First calculate a floating average to eliminate pressure noise to influence our ascent/descent
        int range= 3;
        for(int i= 0; i < samples.size(); i++){
            int min= Math.max(i-range, 0);
            int max= Math.min(i+range, samples.size()-1);
            samples.get(i).tmpElevation= getAverageElevation(samples.subList(min, max));
        }

        // Now sum up the ascent/descent
        for(int i= 0; i < samples.size(); i++) {
            WorkoutSample sample = samples.get(i);
            sample.elevation= sample.tmpElevation;
            if(i >= 1){
                WorkoutSample lastSample= samples.get(i-1);
                double diff= sample.elevation - lastSample.elevation;
                if(diff > 0){
                    // If this sample is higher than the last one, add difference to ascent
                    workout.ascent += diff;
                }else{
                    // If this sample is lower than the last one, add difference to descent
                    workout.descent += Math.abs(diff);
                }
            }
        }

    }

    private void setCalories() {
        // Ascent has to be set previously
        workout.calorie = CalorieCalculator.calculateCalories(workout, Instance.getInstance(context).userPreferences.getUserWeight());
    }

    private void storeInDatabase(){
        db.workoutDao().insertWorkoutAndSamples(workout, samples.toArray(new WorkoutSample[0]));
    }
}
