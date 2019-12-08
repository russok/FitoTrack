package de.tadris.fitness.osm;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;
import de.tadris.fitness.view.ProgressDialogController;
import de.westnordost.osmapi.OsmConnection;
import de.westnordost.osmapi.traces.GpsTraceDetails;
import de.westnordost.osmapi.traces.GpsTracesDao;
import de.westnordost.osmapi.traces.GpsTrackpoint;
import oauth.signpost.OAuthConsumer;

public class OsmTraceUploader {

    private static final int CUT_DISTANCE= 300;

    private Activity activity;
    private Handler handler;
    private Workout workout;
    private List<WorkoutSample> samples;
    private GpsTraceDetails.Visibility visibility;
    private OAuthConsumer consumer;
    private boolean cut;
    private ProgressDialogController dialogController;
    private String description;

    public OsmTraceUploader(Activity activity, Handler handler, Workout workout, List<WorkoutSample> samples, GpsTraceDetails.Visibility visibility, OAuthConsumer consumer, boolean cut, String description) {
        this.activity = activity;
        this.handler = handler;
        this.workout = workout;
        this.samples = samples;
        this.visibility = visibility;
        this.consumer = consumer;
        this.cut = cut;
        this.description= description;
        this.dialogController= new ProgressDialogController(activity, activity.getString(R.string.uploading));
    }

    private void cut(){
        cut(false);
        cut(true);
    }

    private void cut(boolean last){
        double distance= 0;
        int count= 0;
        WorkoutSample lastSample= samples.remove(last ? samples.size()-1 : 0);
        while(distance < CUT_DISTANCE){
            WorkoutSample currentSample= samples.remove(last ? samples.size()-1 : 0);
            distance+= lastSample.toLatLong().sphericalDistance(currentSample.toLatLong());
            count++;
            lastSample= currentSample;
        }
        Log.d("Uploader", "Cutted " + (last ? "last" : "first") + " " + count + " Samples (" + distance + " meters)");
    }

    public void upload(){
        new Thread(() -> {
            try{
                executeTask();
            }catch (Exception e){
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(activity, R.string.uploadFailed, Toast.LENGTH_LONG).show();
                    dialogController.cancel();
                });
            }
        }).start();
    }

    private void executeTask(){
        handler.post(() -> dialogController.show());
        setProgress(0);
        if(cut){ cut(); }
        setProgress(20);
        OsmConnection osm = new OsmConnection(
                "https://api.openstreetmap.org/api/0.6/", "FitoTrack", consumer);

        List<GpsTrackpoint> trackpoints= new ArrayList<>();

        for(WorkoutSample sample : samples){
            GpsTrackpoint trackpoint= new GpsTrackpoint(new GpsTraceLatLong(sample));
            trackpoint.time= new Date(sample.absoluteTime);
            trackpoint.elevation= (float)sample.elevation;
            trackpoints.add(trackpoint);
        }
        setProgress(25);
        new GpsTracesDao(osm).create(workout.getDateString(), visibility, description, Collections.singletonList("FitoTrack"), trackpoints);
        setProgress(100);
        handler.post(() -> {
            Toast.makeText(activity, R.string.uploadSuccessful, Toast.LENGTH_LONG).show();
            dialogController.cancel();
        });
    }

    private void setProgress(int progress){
        handler.post(() -> dialogController.setProgress(progress));
    }

}
