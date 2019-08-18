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

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface WorkoutDao {

    @Query("SELECT * FROM workout_sample WHERE workout_id = :workout_id")
    WorkoutSample[] getAllSamplesOfWorkout(long workout_id);

    @Query("SELECT * FROM workout ORDER BY start DESC")
    Workout[] getWorkouts();

    @Insert
    void insertWorkoutAndSamples(Workout workout, WorkoutSample[] samples);

    @Insert
    void insertWorkout(Workout workout);

    @Delete
    void deleteWorkout(Workout workout);

    @Update
    void updateWorkout(Workout workout);


}
