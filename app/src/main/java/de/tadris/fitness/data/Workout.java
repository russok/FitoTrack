package de.tadris.fitness.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout")
public class Workout{

    public static final String WORKOUT_TYPE_RUNNING= "running";
    public static final String WORKOUT_TYPE_CYCLING= "cycling";

    @PrimaryKey
    public long id;

    public long start;
    public long end;
    public double length;
    public double avgSpeed;
    public double avgPace;
    public String workoutType;


}