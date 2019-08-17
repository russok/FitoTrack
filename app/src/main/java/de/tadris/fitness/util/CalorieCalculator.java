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

import de.tadris.fitness.data.Workout;

public class CalorieCalculator {

    /**
     *
     * @param workout the workout
     * @param weight the weight of the person in kilogram
     * @return calories burned
     */
    public static int calculateCalories(Workout workout, double weight){
        int mins= (int)(workout.getDuration() / 1000 / 60);
        return (int)(mins * (getMET(workout) * 3.5 * weight) / 200);
    }

    /**
     * calorie calculation based on @link { https://www.topendsports.com/weight-loss/energy-met.htm }
     *
     * @param workout
     * @return MET
     */
    public static double getMET(Workout workout){
        if(workout.workoutType.equals(Workout.WORKOUT_TYPE_RUNNING)){
            if(workout.avgSpeed < 3.2){
                return 1.5;
            }else if(workout.avgSpeed < 4.0){
                return 2.5;
            }else if(workout.avgSpeed < 4.8){
                return 3.25;
            }else if(workout.avgSpeed < 5.9){
                return 4.0;
            }else if(workout.avgSpeed < 7.0){
                return 5.0;
            }else if(workout.avgSpeed < 8.0){
                return 7.0;
            }else if(workout.avgSpeed < 9.6){
                return 9.0;
            }else if(workout.avgSpeed < 12.8){
                return 12.0;
            }else{
                return 16;
            }
        }
        if(workout.workoutType.equals(Workout.WORKOUT_TYPE_HIKING)){
            return 6.3;
        }
        if(workout.workoutType.equals(Workout.WORKOUT_TYPE_CYCLING)){
            return Math.max(3, (workout.avgSpeed-10) / 1.5);
        }
        return -1;
    }

}
