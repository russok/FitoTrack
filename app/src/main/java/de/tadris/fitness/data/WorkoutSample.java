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

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.mikephil.charting.data.Entry;

import org.mapsforge.core.model.LatLong;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "workout_sample",
        foreignKeys = @ForeignKey(
                entity = Workout.class,
                parentColumns = "id",
                childColumns = "workout_id",
                onDelete = CASCADE))
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkoutSample{

    @PrimaryKey
    public long id;

    @ColumnInfo(name = "workout_id")
    public long workoutId;

    public long absoluteTime;

    public long relativeTime;

    public double lat;

    public double lon;

    public double elevation;

    public double speed;

    @JsonIgnore
    @Ignore
    public Entry tmpHeightEntry;

    @JsonIgnore
    @Ignore
    public Entry tmpSpeedEntry;

    @JsonIgnore
    @Ignore
    public double tmpRoundedSpeed;

    @JsonIgnore
    @Ignore
    public double tmpElevation;

    @JsonIgnore
    @Ignore
    public float tmpPressure;

    @JsonIgnore
    @Ignore
    public float tmpInclination;

    public LatLong toLatLong(){
        return new LatLong(lat, lon);
    }


}
