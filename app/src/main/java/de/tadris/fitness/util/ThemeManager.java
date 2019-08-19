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

import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;

public class ThemeManager {

    public static int getThemeByWorkoutType(String type){
        switch (type){
            case Workout.WORKOUT_TYPE_RUNNING: return R.style.Running;
            case Workout.WORKOUT_TYPE_CYCLING: return R.style.Bicycling;
            case Workout.WORKOUT_TYPE_HIKING:  return R.style.Hiking;
            default: return R.style.AppTheme;
        }
    }

    public static int getThemeByWorkout(Workout workout){
        return getThemeByWorkoutType(workout.workoutType);
    }

}

