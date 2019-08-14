package de.tadris.fitness.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Workout.class, WorkoutSample.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WorkoutDao workoutDao();
}
