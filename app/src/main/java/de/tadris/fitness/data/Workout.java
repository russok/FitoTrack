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

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "workout")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Workout{

    public static final String WORKOUT_TYPE_RUNNING= "running";
    public static final String WORKOUT_TYPE_HIKING=  "hiking";
    public static final String WORKOUT_TYPE_CYCLING= "cycling";

    @PrimaryKey
    public long id;

    public long start;
    public long end;

    public long duration;

    public long pauseDuration;

    public String comment;

    /**
     * Length of workout in meters
     */
    public int length;

    /**
     * Average speed of workout in m/s
     */
    public double avgSpeed;

    /**
     * Top speed in m/s
     */
    public double topSpeed;

    /**
     * Average pace of workout in km/min
     */
    public double avgPace;

    public String workoutType;


    public float ascent;

    public float descent;

    public int calorie;

    public String toString(){
        if(comment.length() > 2){
            return comment;
        }else{
            return SimpleDateFormat.getDateTimeInstance().format(new Date(start));
        }
    }


}