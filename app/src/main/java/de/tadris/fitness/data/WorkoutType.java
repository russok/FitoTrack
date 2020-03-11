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

package de.tadris.fitness.data;

import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import de.tadris.fitness.R;

public enum WorkoutType {

    RUNNING("running", R.string.workoutTypeRunning, 7, true, R.style.Running, R.style.RunningDark),
    HIKING("hiking", R.string.workoutTypeHiking, 7, true, R.style.Hiking, R.style.HikingDark),
    CYCLING("cycling", R.string.workoutTypeCycling, 12, true, R.style.Bicycling, R.style.BicyclingDark),
    OTHER("other", R.string.workoutTypeOther, 7, true, R.style.AppTheme, R.style.AppThemeDark);

    public String id;
    @StringRes
    public int title;
    public int minDistance; // Minimum distance between samples
    public boolean hasGPS;
    @StyleRes
    public int lightTheme, darkTheme;

    WorkoutType(String id, int title, int minDistance, boolean hasGPS, int lightTheme, int darkTheme) {
        this.id = id;
        this.title = title;
        this.minDistance = minDistance;
        this.hasGPS = hasGPS;
        this.lightTheme = lightTheme;
        this.darkTheme = darkTheme;
    }

    public static WorkoutType getTypeById(String id) {
        for (WorkoutType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return OTHER;
    }
}
