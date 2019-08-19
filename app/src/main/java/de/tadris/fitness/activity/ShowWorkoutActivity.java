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

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutManager;
import de.tadris.fitness.data.WorkoutSample;
import de.tadris.fitness.map.MapManager;
import de.tadris.fitness.map.WorkoutLayer;
import de.tadris.fitness.util.ThemeManager;
import de.tadris.fitness.util.UnitUtils;
import de.tadris.fitness.util.WorkoutTypeCalculator;

public class ShowWorkoutActivity extends FitoTrackActivity {
    static Workout selectedWorkout;

    List<WorkoutSample> samples;
    Workout workout;
    ViewGroup root;
    Resources.Theme theme;
    MapView map;
    TileDownloadLayer downloadLayer;
    FixedPixelCircle highlightingCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workout= selectedWorkout;
        samples= Arrays.asList(Instance.getInstance(this).db.workoutDao().getAllSamplesOfWorkout(workout.id));
        setTheme(ThemeManager.getThemeByWorkout(workout));
        setContentView(R.layout.activity_show_workout);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(WorkoutTypeCalculator.getType(workout));

        theme= getTheme();

        root= findViewById(R.id.showWorkoutRoot);

        addText(getString(R.string.comment) + ": " + workout.comment).setOnClickListener(v -> {
            TextView textView= (TextView)v;
            openEditCommentDialog(textView);
        });

        addTitle(getString(R.string.workoutTime));
        addKeyValue(getString(R.string.workoutDate), getDate());
        addKeyValue(getString(R.string.workoutDuration), UnitUtils.getHourMinuteSecondTime(workout.duration),
                getString(R.string.workoutPauseDuration), UnitUtils.getHourMinuteSecondTime(workout.pauseDuration));
        addKeyValue(getString(R.string.workoutStartTime), SimpleDateFormat.getTimeInstance().format(new Date(workout.start)),
                getString(R.string.workoutEndTime), SimpleDateFormat.getTimeInstance().format(new Date(workout.end)));

        addKeyValue(getString(R.string.workoutDistance), UnitUtils.getDistance(workout.length), getString(R.string.workoutPace), UnitUtils.getPace(workout.avgPace));

        addTitle(getString(R.string.workoutRoute));

        addMap();

        addTitle(getString(R.string.workoutSpeed));

        addKeyValue(getString(R.string.workoutAvgSpeed), UnitUtils.getSpeed(workout.avgSpeed),
                getString(R.string.workoutTopSpeed), UnitUtils.getSpeed(workout.topSpeed));

        addSpeedDiagram();

        addTitle(getString(R.string.workoutBurnedEnergy));
        addKeyValue(getString(R.string.workoutTotalEnergy), workout.calorie + " kcal",
                getString(R.string.workoutEnergyConsumption), UnitUtils.getRelativeEnergyConsumption((double)workout.calorie / ((double)workout.length / 1000)));


    }

    void openEditCommentDialog(final TextView change){
        final EditText editText= new EditText(this);
        editText.setText(workout.comment);
        new AlertDialog.Builder(this)
                .setTitle(R.string.enterComment)
                .setPositiveButton(R.string.okay, (dialog, which) -> changeComment(editText.getText().toString(), change))
                .setView(editText).create().show();
    }

    void changeComment(String comment, TextView onChange){
        workout.comment= comment;
        Instance.getInstance(this).db.workoutDao().updateWorkout(workout);
        onChange.setText(getString(R.string.comment) + ": " + workout.comment);
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
        textView.setPadding(0, 20, 0, 0);

        root.addView(textView);
    }

    TextView addText(String text){
        TextView textView= new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setTextColor(getThemePrimaryColor());
        textView.setPadding(0, 20, 0, 0);

        root.addView(textView);

        return textView;
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

    void addSpeedDiagram(){
        LineChart chart= new LineChart(this);

        WorkoutManager.roundSpeedValues(samples);

        List<Entry> entries = new ArrayList<>();
        for (WorkoutSample sample : samples) {
            // turn your data into Entry objects
            Entry e= new Entry((float)(sample.relativeTime) / 1000f / 60f, (float)sample.tmpRoundedSpeed*3.6f);
            entries.add(e);
            sample.tmpEntry= e;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Speed"); // add entries to dataset // TODO: localisatoin
        dataSet.setColor(getThemePrimaryColor());
        dataSet.setValueTextColor(getThemePrimaryColor());
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(4);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        Description description= new Description();
        description.setText("min - km/h");

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setScaleEnabled(false);
        chart.setDescription(description);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                onNothingSelected();
                Paint p= AndroidGraphicFactory.INSTANCE.createPaint();
                p.setColor(Color.BLUE);
                highlightingCircle= new FixedPixelCircle(getSamplebyTime(e).toLatLong(), 10, p, null);
                map.addLayer(highlightingCircle);
            }

            @Override
            public void onNothingSelected() {
                if(highlightingCircle != null){
                    map.getLayerManager().getLayers().remove(highlightingCircle);
                }
            }
        });
        chart.invalidate();

        root.addView(chart, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getWindowManager().getDefaultDisplay().getWidth()*3/4));
    }

    WorkoutSample getSamplebyTime(Entry entry){
        for(WorkoutSample sample : samples){
            if(sample.tmpEntry.equalTo(entry)){
                return sample;
            }
        }
        return null;
    }

    void addMap(){
        map= new MapView(this);
        downloadLayer= MapManager.setupMap(map);
        map.setZoomLevelMin((byte)2);
        map.setZoomLevelMax((byte)18);

        WorkoutLayer workoutLayer= new WorkoutLayer(samples, getThemePrimaryColor());
        map.addLayer(workoutLayer);

        final BoundingBox bounds= new BoundingBox(workoutLayer.getLatLongs()).extendMeters(50);
        new Handler().postDelayed(() -> {
            map.getModel().mapViewPosition.setMapPosition(new MapPosition(bounds.getCenterPoint(),
                    (byte)(LatLongUtils.zoomForBounds(map.getDimension(), bounds, map.getModel().displayModel.getTileSize()))));
            map.animate().alpha(1f).setDuration(1000).start();
        }, 1000);

        map.getModel().mapViewPosition.setMapLimit(bounds);


        root.addView(map, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getWindowManager().getDefaultDisplay().getWidth()*3/4));
        map.setAlpha(0);


        Paint pGreen= AndroidGraphicFactory.INSTANCE.createPaint();
        pGreen.setColor(Color.GREEN);
        map.addLayer(new FixedPixelCircle(samples.get(0).toLatLong(), 20, pGreen, null));
        Paint pRed= AndroidGraphicFactory.INSTANCE.createPaint();
        pRed.setColor(Color.RED);
        map.addLayer(new FixedPixelCircle(samples.get(samples.size()-1).toLatLong(), 20, pRed, null));
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

    private void deleteWorkout(){
        Instance.getInstance(this).db.workoutDao().deleteWorkout(workout);
        finish();
    }

    private void showDeleteDialog(){
        new AlertDialog.Builder(this).setTitle(R.string.deleteWorkout)
                .setMessage(R.string.deleteWorkoutMessage)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteWorkout())
                .create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.actionDeleteWorkout){
            showDeleteDialog();
            return true;
        }else if(id == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
