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

package de.tadris.fitness.util.gpx;

import android.content.Context;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.tadris.fitness.Instance;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;

public class GpxExporter {

    public static void exportWorkout(Context context, Workout workout, File file) throws IOException {
        XmlMapper mapper= new XmlMapper();
        mapper.writeValue(file, getGpxFromWorkout(context, workout));
    }

    public static Gpx getGpxFromWorkout(Context context, Workout workout){
        Gpx gpx= new Gpx();
        gpx.name= workout.toString();
        gpx.version= "1.1";
        gpx.creator= "FitoTrack";
        gpx.metadata= new Metadata(workout.toString(), workout.comment, getDateTime(workout.start));
        gpx.trk= new ArrayList<>();
        gpx.trk.add(getTrackFromWorkout(context, workout, 0));

        return gpx;
    }

    public static Track getTrackFromWorkout(Context context, Workout workout, int number){
        WorkoutSample[] samples= Instance.getInstance(context).db.workoutDao().getAllSamplesOfWorkout(workout.id);
        Track track= new Track();
        track.number= number;
        track.name= workout.toString();
        track.cmt= workout.comment;
        track.desc= workout.comment;
        track.src= "FitoTrack";
        track.type= workout.workoutType;
        track.trkseg= new ArrayList<>();

        TrackSegment segment= new TrackSegment();
        segment.trkpt= new ArrayList<>();

        for(WorkoutSample sample : samples){
            segment.trkpt.add(new TrackPoint(sample.lat, sample.lon, sample.elevation,
                    getDateTime(sample.absoluteTime), "gps",
                    new TrackPointExtension(sample.speed)));
        }

        track.trkseg.add(segment);

        return track;
    }

    private static final SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static String getDateTime(long time){
        return getDateTime(new Date(time));
    }

    public static String getDateTime(Date date){
        return formatter.format(date);
    }




}
