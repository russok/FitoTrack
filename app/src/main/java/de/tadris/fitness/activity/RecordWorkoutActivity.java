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

package de.tadris.fitness.activity;

import android.Manifest;
import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import android.speech.tts.TextToSpeech;

import androidx.core.app.ActivityCompat;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.UserPreferences;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.map.MapManager;
import de.tadris.fitness.map.tilesource.TileSources;
import de.tadris.fitness.recording.LocationListener;
import de.tadris.fitness.recording.PressureService;
import de.tadris.fitness.recording.WorkoutRecorder;
import de.tadris.fitness.util.ThemeManager;
import de.tadris.fitness.util.unit.UnitUtils;

public class RecordWorkoutActivity extends FitoTrackActivity implements LocationListener.LocationChangeListener, WorkoutRecorder.WorkoutRecorderListener {

    public static String ACTIVITY= Workout.WORKOUT_TYPE_RUNNING;

    private MapView mapView;
    private TileDownloadLayer downloadLayer;
    private WorkoutRecorder recorder;
    private Polyline polyline;
    private final List<LatLong> latLongList = new ArrayList<>();
    private final InfoViewHolder[] infoViews = new InfoViewHolder[4];
    private TextView timeView;
    private TextView gpsStatusView;
    private View waitingForGPSOverlay;
    private boolean gpsFound = false;
    private boolean isResumed = false;
    private final Handler mHandler = new Handler();
    private PowerManager.WakeLock wakeLock;
    private Intent locationListener;
    private Intent pressureService;
    private boolean saved= false;
    TextToSpeech tts;
    boolean ttsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeManager.getThemeByWorkoutType(ACTIVITY));
        setContentView(R.layout.activity_record_workout);

        setTitle(R.string.recordWorkout);

        setupMap();

        ((ViewGroup) findViewById(R.id.recordMapViewerRoot)).addView(mapView);
        waitingForGPSOverlay= findViewById(R.id.recorderWaitingOverlay);
        waitingForGPSOverlay.setVisibility(View.VISIBLE);

        checkPermissions();

        recorder= new WorkoutRecorder(this, ACTIVITY, this);
        recorder.start();

        tts = new TextToSpeech(this, (int status) -> {
            ttsReady = status == TextToSpeech.SUCCESS && tts.setLanguage(Locale.getDefault())>=0;
        });
        infoViews[0]= new InfoViewHolder(findViewById(R.id.recordInfo1Title), findViewById(R.id.recordInfo1Value));
        infoViews[1]= new InfoViewHolder(findViewById(R.id.recordInfo2Title), findViewById(R.id.recordInfo2Value));
        infoViews[2]= new InfoViewHolder(findViewById(R.id.recordInfo3Title), findViewById(R.id.recordInfo3Value));
        infoViews[3]= new InfoViewHolder(findViewById(R.id.recordInfo4Title), findViewById(R.id.recordInfo4Value));
        timeView= findViewById(R.id.recordTime);
        gpsStatusView= findViewById(R.id.recordGpsStatus);

        updateDescription();

        startUpdater();
        acquireWakelock();

        Instance.getInstance(this).locationChangeListeners.add(this);

        startListener();

    }

    private void acquireWakelock(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "de.tadris.fitotrack:workout_recorder");
        wakeLock.acquire(1000*60*120);
    }

    private void hideWaitOverlay(){
        waitingForGPSOverlay.clearAnimation();
        waitingForGPSOverlay.animate().alpha(0f).setDuration(1000).setListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animator) { }
            @Override public void onAnimationCancel(Animator animator) { }
            @Override public void onAnimationRepeat(Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                waitingForGPSOverlay.setVisibility(View.GONE);
            }
        }).start();
    }

    private void setupMap(){
        this.mapView= new MapView(this);
        TileSources.Purpose purpose;
        if(ACTIVITY.equals(Workout.WORKOUT_TYPE_CYCLING)){
            purpose= TileSources.Purpose.CYCLING;
        }else{
            purpose= TileSources.Purpose.OUTDOOR;
        }
        downloadLayer= MapManager.setupMap(mapView, purpose);
    }

    private void updateLine(){
        if(polyline != null){
            mapView.getLayerManager().getLayers().remove(polyline);
        }
        Paint p= AndroidGraphicFactory.INSTANCE.createPaint();
        p.setColor(getThemePrimaryColor());
        p.setStrokeWidth(20);
        p.setStyle(Style.STROKE);
        polyline= new Polyline(p, AndroidGraphicFactory.INSTANCE);
        polyline.setPoints(latLongList);
        mapView.addLayer(polyline);
    }

    private void startUpdater(){
        new Thread(() -> {
            try{
                while (recorder.isActive()){
                    Thread.sleep(1000);
                    mHandler.post(this::updateDescription);
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }

    long lastSpokenUpdateTime = 0;
    int lastSpokenUpdateDistance = 0;

    private void updateDescription() {
        long duration = recorder.getDuration();
        int distanceInMeters = recorder.getDistance();
        final String distanceCaption = getString(R.string.workoutDistance);
        final String distance = UnitUtils.getDistance(distanceInMeters);
        final String avgSpeedCaption = getString(R.string.workoutAvgSpeed);
        final String avgSpeed = UnitUtils.getSpeed(Math.min(100d, recorder.getAvgSpeed()));
        if (isResumed) {
            timeView.setText(UnitUtils.getHourMinuteSecondTime(duration));
            infoViews[0].setText(distanceCaption, distance);
            infoViews[1].setText(getString(R.string.workoutBurnedEnergy), recorder.getCalories() + " kcal");
            infoViews[2].setText(avgSpeedCaption, avgSpeed);
            infoViews[3].setText(getString(R.string.workoutPauseDuration), UnitUtils.getHourMinuteSecondTime(recorder.getPauseDuration()));
        }

        final UserPreferences prefs = Instance.getInstance(this).userPreferences;
        final long intervalT = 60 * 1000 * prefs.getSpokenUpdateTimePeriod();
        final int intervalInMeters = (int) (1000.0 / UnitUtils.CHOSEN_SYSTEM.getDistanceFromKilometers(1) * prefs.getSpokenUpdateDistancePeriod());
        if (!ttsReady ||
                (intervalT == 0 || duration / intervalT == lastSpokenUpdateTime / intervalT)
             && (intervalInMeters == 0 || distanceInMeters / intervalInMeters == lastSpokenUpdateDistance / intervalInMeters)
        ) return;

        final String text = distanceCaption + ": " + distance + ". "
                + avgSpeedCaption + ": " + avgSpeed;
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "updateDescription" + duration);

        lastSpokenUpdateTime = duration;
        lastSpokenUpdateDistance = distanceInMeters;
    }

    private void stop(){
        recorder.stop();
        if(recorder.getSampleCount() > 3){
            showEnterDescriptionDialog();
        }else{
            finish();
        }
    }

    private void saveAndClose(){
        save();
        finish();
    }

    private void save(){
        if(recorder.getSampleCount() > 3){
            recorder.save();
            saved= true;
        }
    }

    private void saveIfNotSaved(){
        if(!saved){
            save();
        }
    }

    private void showEnterDescriptionDialog(){
        final EditText editText= new EditText(this);
        editText.setSingleLine(true);
        new AlertDialog.Builder(this).setTitle(R.string.enterComment).setPositiveButton(R.string.okay, (dialog, which) -> {
            dialog.dismiss();
            recorder.setComment(editText.getText().toString());
            saveAndClose();
        }).setView(editText).setOnCancelListener(dialog -> saveAndClose()).create().show();
    }

    private void showAreYouSureToStopDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.stopRecordingQuestion)
                .setMessage(R.string.stopRecordingQuestionMessage)
                .setPositiveButton(R.string.stop, (dialog, which) -> stop())
                .setNegativeButton(R.string.continue_, null)
                .create().show();
    }

    private void checkPermissions() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        }
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (hasPermission()) {
            startListener();
        }
    }

    private void stopListener() {
        stopService(locationListener);
        stopService(pressureService);
    }

    private void startListener() {
        if(locationListener == null){
            locationListener= new Intent(this, LocationListener.class);
            pressureService= new Intent(this, PressureService.class);
        }else{
            stopListener();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(locationListener);
            startService(pressureService);
        }else{
            startService(locationListener);
            startService(pressureService);
        }
        checkGpsStatus();
    }

    private void checkGpsStatus(){
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            openDialogNoGps();
        }
    }

    private void openDialogNoGps(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.noGpsTitle)
                .setMessage(R.string.noGpsMessage)
                .setNegativeButton(R.string.cancel, (dialog, which) -> finish())
                .setPositiveButton(R.string.enable, (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setCancelable(false)
                .create().show();
    }

    @Override
    public void onLocationChange(Location location) {
        LatLong latLong= LocationListener.locationToLatLong(location);
        mapView.getModel().mapViewPosition.animateTo(latLong);
        latLongList.add(latLong);
        updateLine();
    }

    @Override
    protected void onDestroy() {
        recorder.stop();
        saveIfNotSaved(); // Important
        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
        if(wakeLock.isHeld()){
            wakeLock.release();
        }
        Instance.getInstance(this).locationChangeListeners.remove(this);
        stopListener();
    }

    @Override
    public void onPause(){
        super.onPause();
        downloadLayer.onPause();
        isResumed= false;
    }

    public void onResume(){
        super.onResume();
        downloadLayer.onResume();
        isResumed= true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.actionRecordingStop){
            stop();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(recorder.getSampleCount() > 3){
            showAreYouSureToStopDialog();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onGPSStateChanged(WorkoutRecorder.GpsState oldState, WorkoutRecorder.GpsState state) {
        mHandler.post(() -> {
            gpsStatusView.setTextColor(state.color);
            if(!gpsFound && (state != WorkoutRecorder.GpsState.SIGNAL_LOST)){
                gpsFound= true;
                hideWaitOverlay();
            }
        });
    }

    static class InfoViewHolder {
        final TextView titleView;
        final TextView valueView;

        InfoViewHolder(TextView titleView, TextView valueView) {
            this.titleView = titleView;
            this.valueView = valueView;
        }

        void setText(String title, String value){
            this.titleView.setText(title);
            this.valueView.setText(value);
        }
    }

    @Override
    public void onAutoStop() {
        finish();
    }
}
