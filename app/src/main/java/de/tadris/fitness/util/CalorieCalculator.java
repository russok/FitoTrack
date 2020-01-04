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

package de.tadris.fitness.util;

import de.tadris.fitness.data.Workout;

public class CalorieCalculator {

    /**
     *
     * workoutType, duration and avgSpeed of workout have to be set
     *
     * @param workout the workout
     * @param weight the weight of the person in kilogram
     * @return calories burned
     */
    public static int calculateCalories(Workout workout, double weight){
        double mins= (double)(workout.duration / 1000) / 60;
        int ascent= (int)workout.ascent; // 1 calorie per meter
        return (int)(mins * (getMET(workout) * 3.5 * weight) / 200) + ascent;
    }

    /**
     * calorie calculation based on @link { https://www.topendsports.com/weight-loss/energy-met.htm }
     *
     * workoutType and avgSpeed of workout have to be set
     *
     * Calculation currently ignores height.
     * @return MET
     */
    private static double getMET(Workout workout) {
        double speedInKmh= workout.avgSpeed * 3.6;
        if(workout.workoutType.equals(Workout.WORKOUT_TYPE_RUNNING) || workout.workoutType.equals(Workout.WORKOUT_TYPE_HIKING)){
            // This is a linear graph based on the website linked above
            return Math.max(1.5, speedInKmh*1.117 - 2.1906);
        }
        if(workout.workoutType.equals(Workout.WORKOUT_TYPE_CYCLING)){
            // This is a linear graph based on the website linked above
            return Math.max(3, (speedInKmh-10) / 1.5);
        }
        return -1;
    }

}
