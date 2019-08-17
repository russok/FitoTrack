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

import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.util.CalorieCalculator;

public class WorkoutManager {

    public static void insertWorkout(Context context, Workout workout, List<WorkoutSample> samples){
        AppDatabase db= Instance.getInstance(context).db;


        workout.id= System.currentTimeMillis();

        // Calculating values
        double length= 0;
        for(int i= 1; i < samples.size(); i++){
            length+= samples.get(i - 1).toLatLong().sphericalDistance(samples.get(i).toLatLong());
        }
        workout.length= (int)length;
        workout.avgSpeed= ((double) workout.length) / ((double) workout.getDuration() / 1000);
        workout.avgPace= (double)(workout.getDuration() / 1000 / 60) / ((double) workout.length / 1000);
        workout.calorie= CalorieCalculator.calculateCalories(workout, 80);
        // TODO: use user weight

        // Setting workoutId in the samples
        int i= 0;
        double topSpeed= 0;
        for(WorkoutSample sample : samples){
            i++;
            sample.id= workout.id + i;
            sample.workoutId= workout.id;
            if(sample.speed > topSpeed){
                topSpeed= sample.speed;
            }
        }

        workout.topSpeed= topSpeed;


        // Saving workout and samples
        db.workoutDao().insertWorkoutAndSamples(workout, samples.toArray(new WorkoutSample[0]));

    }

}
