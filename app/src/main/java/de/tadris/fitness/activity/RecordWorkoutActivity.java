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

package de.tadris.fitness.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

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

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.location.LocationListener;
import de.tadris.fitness.location.WorkoutRecorder;
import de.tadris.fitness.map.MapManager;
import de.tadris.fitness.util.ThemeManager;
import de.tadris.fitness.util.unit.UnitUtils;

public class RecordWorkoutActivity extends FitoTrackActivity implements LocationListener.LocationChangeListener {

    public static String ACTIVITY= Workout.WORKOUT_TYPE_RUNNING;

    MapView mapView;
    TileDownloadLayer downloadLayer;
    WorkoutRecorder recorder;
    Polyline polyline;
    List<LatLong> latLongList= new ArrayList<>();
    InfoViewHolder[] infoViews= new InfoViewHolder[4];
    TextView timeView, gpsStatusView;
    boolean isResumed= false;
    private Handler mHandler= new Handler();
    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeManager.getThemeByWorkoutType(ACTIVITY));
        setContentView(R.layout.activity_record_workout);

        setTitle(R.string.recordWorkout);

        setupMap();

        ((ViewGroup)findViewById(R.id.recordMapViewrRoot)).addView(mapView);

        checkPermissions();

        recorder= new WorkoutRecorder(this, ACTIVITY);
        recorder.start();

        infoViews[0]= new InfoViewHolder(findViewById(R.id.recordInfo1Title), findViewById(R.id.recordInfo1Value));
        infoViews[1]= new InfoViewHolder(findViewById(R.id.recordInfo2Title), findViewById(R.id.recordInfo2Value));
        infoViews[2]= new InfoViewHolder(findViewById(R.id.recordInfo3Title), findViewById(R.id.recordInfo3Value));
        infoViews[3]= new InfoViewHolder(findViewById(R.id.recordInfo4Title), findViewById(R.id.recordInfo4Value));
        timeView= findViewById(R.id.recordTime);

        updateDescription();

        startUpdater();
        acquireWakelock();
    }

    private void acquireWakelock(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "de.tadris.fitotrack:workout_recorder");
        wakeLock.acquire(1000*60*120);
    }

    private void setupMap(){
        this.mapView= new MapView(this);
        downloadLayer= MapManager.setupMap(mapView);
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
                    if(isResumed){
                        mHandler.post(this::updateDescription);
                    }
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }).start();
    }

    private void updateDescription(){
        timeView.setText(UnitUtils.getHourMinuteSecondTime(recorder.getDuration()));
        infoViews[0].setText(getString(R.string.workoutDistance), UnitUtils.getDistance(recorder.getDistance()));
        infoViews[1].setText(getString(R.string.workoutBurnedEnergy), recorder.getCalories() + " kcal");
        infoViews[2].setText(getString(R.string.workoutAvgSpeed), UnitUtils.getSpeed(Math.min(100d, recorder.getAvgSpeed())));
        infoViews[3].setText(getString(R.string.workoutPauseDuration), UnitUtils.getHourMinuteSecondTime(recorder.getPauseDuration()));
    }

    private void stopAndSave(){
        recorder.stop();
        if(recorder.getSampleCount() > 3){
            recorder.save();
        }
        finish();
    }

    void checkPermissions(){
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
        }
    }

    public boolean hasPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (hasPermission()) {
            Instance.getInstance(this).locationListener.enableMyLocation();
        }
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
        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
        if(wakeLock.isHeld()){
            wakeLock.release();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        downloadLayer.onPause();
        Instance.getInstance(this).locationListener.unregisterLocationChangeListeners(this);
        isResumed= false;
    }

    public void onResume(){
        super.onResume();
        downloadLayer.onResume();
        Instance.getInstance(this).locationListener.registerLocationChangeListeners(this);
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
            stopAndSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class InfoViewHolder{
        TextView titleView, valueView;

        public InfoViewHolder(TextView titleView, TextView valueView) {
            this.titleView = titleView;
            this.valueView = valueView;
        }

        void setText(String title, String value){
            this.titleView.setText(title);
            this.valueView.setText(value);
        }
    }


}
