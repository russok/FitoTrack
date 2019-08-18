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

import android.content.Context;

import de.tadris.fitness.Instance;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;
import io.jenetics.jpx.GPX;
import io.jenetics.jpx.Track;
import io.jenetics.jpx.TrackSegment;

public class GpxExporter {

    public static void exportWorkout(Context context, Workout workout){
        GPX.Builder builder= GPX.builder();

        builder.addTrack(toTrack(context, workout));

    }

    public static Track toTrack(Context context, Workout workout){
        Track.Builder track= Track.builder();
        TrackSegment.Builder segment= TrackSegment.builder();

        WorkoutSample[] samples= Instance.getInstance(context).db.workoutDao().getAllSamplesOfWorkout(workout.id);
        for(WorkoutSample sample : samples){
            segment.addPoint(p -> p.lat(sample.lat).lon(sample.lon).ele(sample.elevation).speed(sample.speed).time(sample.absoluteTime));
        }

        track.addSegment(segment.build());
        track.src("FitoTrack");
        track.type(workout.workoutType);

        return track.build();
    }

}
