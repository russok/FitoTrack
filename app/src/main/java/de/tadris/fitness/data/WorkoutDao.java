package de.tadris.fitness.data;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface WorkoutDao {

    @Query("SELECT * FROM workout_sample WHERE workout_id = :workout_id")
    WorkoutSample[] getAllSamplesOfWorkout(long workout_id);

}
