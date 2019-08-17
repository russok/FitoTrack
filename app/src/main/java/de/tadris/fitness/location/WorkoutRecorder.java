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

package de.tadris.fitness.location;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutManager;
import de.tadris.fitness.data.WorkoutSample;

public class WorkoutRecorder implements LocationListener.LocationChangeListener {

    private static final int MIN_DISTANCE= 5;

    private Context context;
    private Workout workout;
    private RecordingState state;
    private List<WorkoutSample> samples= new ArrayList<>();
    private long time= 0;
    private long lastResume;

    public WorkoutRecorder(Context context, String workoutType) {
        this.context= context;
        this.state= RecordingState.IDLE;

        this.workout= new Workout();
        this.workout.workoutType= workoutType;
    }

    public void start(){
        if(state == RecordingState.IDLE){
            Log.i("Recorder", "");
            workout.start= System.currentTimeMillis();
            resume();
        }else if(state == RecordingState.PAUSED){
            resume();
        }else if(state != RecordingState.RUNNING){
            throw new IllegalStateException("Cannot start or resume recording. state = " + state);
        }
    }

    private void resume(){
        state= RecordingState.RUNNING;
        lastResume= System.currentTimeMillis();
        Instance.getInstance(context).locationListener.registerLocationChangeListeners(this);
    }

    public void pause(){
        if(state == RecordingState.RUNNING){
            state= RecordingState.PAUSED;
            time+= System.currentTimeMillis() - lastResume;
            Instance.getInstance(context).locationListener.unregisterLocationChangeListeners(this);
        }
    }

    public void stop(){
        pause();
        workout.end= System.currentTimeMillis();
        state= RecordingState.STOPPED;
    }

    public void save(){
        if(state != RecordingState.STOPPED){
            throw new IllegalStateException("Cannot save recording, recorder was not stopped. state = " + state);
        }
        WorkoutManager.insertWorkout(context, workout, samples);
    }

    public int getSampleCount(){
        return samples.size();
    }

    @Override
    public void onLocationChange(Location location) {
        if(state == RecordingState.RUNNING){
            if(getSampleCount() > 0){
                WorkoutSample lastSample= samples.get(samples.size() - 1);
                if(LocationListener.locationToLatLong(location).sphericalDistance(new LatLong(lastSample.lat, lastSample.lon)) < MIN_DISTANCE){
                    return;
                }
            }
            WorkoutSample sample= new WorkoutSample();
            sample.lat= location.getLatitude();
            sample.lon= location.getLongitude();
            sample.speed= location.getSpeed();
            sample.time= location.getTime();
            samples.add(sample);
        }
    }


    enum RecordingState{
        IDLE, RUNNING, PAUSED, STOPPED
    }

}
