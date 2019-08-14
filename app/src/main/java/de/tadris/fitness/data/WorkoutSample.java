package de.tadris.fitness.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "workout_sample",
        foreignKeys = @ForeignKey(
                entity = Workout.class,
                parentColumns = "id",
                childColumns = "workout_id",
                onDelete = CASCADE))
public class WorkoutSample{

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "workout_id")
    public long workoutId;

    public long time;

    public double lat;

    public double lon;

    public double speed;


}
