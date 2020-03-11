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

import android.content.Context;

import java.util.Calendar;

import de.tadris.fitness.Instance;
import de.tadris.fitness.util.CalorieCalculator;

public class WorkoutBuilder {

    private WorkoutType workoutType;

    private Calendar start;
    private long duration;

    private int length;

    private String comment;

    public WorkoutBuilder() {
        workoutType = WorkoutType.RUNNING;
        start = Calendar.getInstance();
        duration = 1000L * 60 * 10;
        length = 500;
        comment = "";
    }

    public Workout create(Context context) {
        Workout workout = new Workout();

        // Calculate values
        workout.start = start.getTimeInMillis();
        workout.duration = duration;
        workout.end = workout.start + workout.duration;

        workout.id = workout.start;
        workout.setWorkoutType(workoutType);

        workout.length = length;

        workout.avgSpeed = (double) length / (double) (duration / 1000);
        workout.topSpeed = 0;
        workout.avgPace = ((double) workout.duration / 1000 / 60) / ((double) workout.length / 1000);

        workout.pauseDuration = 0;
        workout.ascent = 0;
        workout.descent = 0;

        workout.calorie = CalorieCalculator.calculateCalories(workout, Instance.getInstance(context).userPreferences.getUserWeight());
        workout.comment = comment;

        workout.edited = true;

        return workout;
    }

    public Workout insertWorkout(Context context) {
        Workout workout = create(context);
        Instance.getInstance(context).db.workoutDao().insertWorkout(workout);
        return workout;
    }

    public WorkoutType getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(WorkoutType workoutType) {
        this.workoutType = workoutType;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
