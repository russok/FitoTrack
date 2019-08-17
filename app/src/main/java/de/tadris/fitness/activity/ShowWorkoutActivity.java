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

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.download.TileDownloadLayer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;
import de.tadris.fitness.map.MapManager;
import de.tadris.fitness.map.WorkoutLayer;
import de.tadris.fitness.util.ThemeManager;
import de.tadris.fitness.util.UnitUtils;

public class ShowWorkoutActivity extends Activity {
    static Workout selectedWorkout;

    List<WorkoutSample> samples;
    Workout workout;
    ViewGroup root;
    Resources.Theme theme;
    MapView map;
    TileDownloadLayer downloadLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workout= selectedWorkout;
        samples= Arrays.asList(Instance.getInstance(this).db.workoutDao().getAllSamplesOfWorkout(workout.id));
        setTheme(ThemeManager.getThemeByWorkout(workout, this));
        setContentView(R.layout.activity_show_workout);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        theme= getTheme();

        root= findViewById(R.id.showWorkoutRoot);

        addTitle("Zeit");
        addKeyValue("Datum", getDate(), "Dauer", UnitUtils.getHourMinuteTime(workout.getDuration()));
        addKeyValue("Startzeit", SimpleDateFormat.getTimeInstance().format(new Date(workout.start)),
                "Endzeit", SimpleDateFormat.getTimeInstance().format(new Date(workout.end)));

        addKeyValue("Distanz", UnitUtils.getDistance(workout.length), "Pace", UnitUtils.round(workout.avgPace, 1) + " min/km");

        addTitle("Geschwindigkeit");

        addKeyValue("Durchschnittsgeschw.", UnitUtils.getSpeed(workout.avgSpeed),
                "Top Geschw.", UnitUtils.round(workout.topSpeed, 1) + " km/h");

        // TODO: add speed diagram

        addTitle("Verbrauchte Energie");
        addKeyValue("Gesamtverbrauch", workout.calorie + " kcal",
                "Relativverbrauch", UnitUtils.round(((double)workout.calorie / workout.length / 1000), 2) + " kcal/km");

        addTitle("Route");

        addMap();

    }

    String getDate(){
        return SimpleDateFormat.getDateInstance().format(new Date(workout.start));
    }


    void addTitle(String title){
        TextView textView= new TextView(this);
        textView.setText(title);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(getThemePrimaryColor());
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setAllCaps(true);

        root.addView(textView);
    }

    void addKeyValue(String key1, String value1){
        addKeyValue(key1, value1, "", "");
    }

    void addKeyValue(String key1, String value1, String key2, String value2){
        View v= getLayoutInflater().inflate(R.layout.show_entry, root, false);

        TextView title1= v.findViewById(R.id.v1title);
        TextView title2= v.findViewById(R.id.v2title);
        TextView v1= v.findViewById(R.id.v1value);
        TextView v2= v.findViewById(R.id.v2value);

        title1.setText(key1);
        title2.setText(key2);
        v1.setText(value1);
        v2.setText(value2);

        root.addView(v);
    }

    void addDiagram(){

    }

    void addMap(){
        map= new MapView(this);
        downloadLayer= MapManager.setupMap(map);
        map.setZoomLevelMin((byte)2);
        map.setZoomLevelMax((byte)18);

        WorkoutLayer workoutLayer= new WorkoutLayer(samples, getThemePrimaryColor());
        map.addLayer(workoutLayer);

        final BoundingBox bounds= new BoundingBox(workoutLayer.getLatLongs()).extendMeters(50);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                map.getModel().mapViewPosition.setMapPosition(new MapPosition(bounds.getCenterPoint(),
                        (byte)(LatLongUtils.zoomForBounds(map.getDimension(), bounds, map.getModel().displayModel.getTileSize()))));
                map.animate().alpha(1f).setDuration(1000).start();
            }
        }, 1000);

        map.getModel().mapViewPosition.setMapLimit(bounds);


        root.addView(map, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getWindowManager().getDefaultDisplay().getWidth()*3/4));
        map.setAlpha(0);
    }

    private int getThemePrimaryColor() {
        final TypedValue value = new TypedValue ();
        getTheme().resolveAttribute (android.R.attr.colorPrimary, value, true);
        return value.data;
    }

    @Override
    protected void onDestroy() {
        map.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
        downloadLayer.onPause();
    }

    public void onResume(){
        super.onResume();
        downloadLayer.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.actionDeleteWorkout){
            // TODO: delete workout
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*private int zoomToBounds(BoundingBox boundingBox) {
        int TILE_SIZE= map.getModel().displayModel.getTileSize();
        Dimension mapViewDimension = map.getModel().mapViewDimension.getDimension();
        if(mapViewDimension == null)
            return 0;
        double dxMax = longitudeToPixelX(boundingBox.maxLongitude, (byte) 0) / TILE_SIZE;
        double dxMin = longitudeToPixelX(boundingBox.minLongitude, (byte) 0) / TILE_SIZE;
        double zoomX = floor(-log(3.8) * log(abs(dxMax-dxMin)) + mapViewDimension.width / TILE_SIZE);
        double dyMax = latitudeToPixelY(boundingBox.maxLatitude, (byte) 0) / TILE_SIZE;
        double dyMin = latitudeToPixelY(boundingBox.minLatitude, (byte) 0) / TILE_SIZE;
        double zoomY = floor(-log(3.8) * log(abs(dyMax-dyMin)) + mapViewDimension.height / TILE_SIZE);
        return Double.valueOf(min(zoomX, zoomY)).intValue();
    }*/

}
