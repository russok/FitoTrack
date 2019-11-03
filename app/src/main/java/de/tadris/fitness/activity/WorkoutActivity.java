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

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.overlay.FixedPixelCircle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutManager;
import de.tadris.fitness.data.WorkoutSample;
import de.tadris.fitness.map.MapManager;
import de.tadris.fitness.map.WorkoutLayer;
import de.tadris.fitness.map.tilesource.TileSources;
import de.tadris.fitness.util.ThemeManager;
import de.tadris.fitness.util.WorkoutTypeCalculator;
import de.tadris.fitness.util.unit.UnitUtils;

public abstract class WorkoutActivity extends FitoTrackActivity {

    public static Workout selectedWorkout;

    protected List<WorkoutSample> samples;
    protected Workout workout;
    protected ViewGroup root;
    protected Resources.Theme theme;
    protected MapView map;
    protected TileDownloadLayer downloadLayer;
    protected FixedPixelCircle highlightingCircle;
    protected Handler mHandler= new Handler();

    protected LineChart speedDiagram, heightDiagram;

    protected void initBeforeContent(){
        workout= selectedWorkout;
        samples= Arrays.asList(Instance.getInstance(this).db.workoutDao().getAllSamplesOfWorkout(workout.id));
        setTheme(ThemeManager.getThemeByWorkout(workout));
    }

    protected void initAfterContent(){
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(WorkoutTypeCalculator.getType(workout));

        theme= getTheme();
    }

    void addDiagram(SampleConverter converter){
        root.addView(getDiagram(converter), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullScreenItems ? ViewGroup.LayoutParams.MATCH_PARENT : getWindowManager().getDefaultDisplay().getWidth()*3/4));
    }

    protected boolean diagramsInteractive= false;

    LineChart getDiagram(SampleConverter converter){
        LineChart chart= new LineChart(this);

        converter.onCreate();

        List<Entry> entries = new ArrayList<>();
        for (WorkoutSample sample : samples) {
            // turn your data into Entry objects
            Entry e= new Entry((float)(sample.relativeTime) / 1000f / 60f, converter.getValue(sample));
            entries.add(e);
            converter.sampleGetsEntry(sample, e);
        }

        LineDataSet dataSet = new LineDataSet(entries, converter.getName()); // add entries to dataset
        dataSet.setColor(getThemePrimaryColor());
        dataSet.setValueTextColor(getThemePrimaryColor());
        dataSet.setDrawCircles(false);
        dataSet.setLineWidth(4);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        Description description= new Description();
        description.setText(converter.getDescription());

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.setScaleXEnabled(diagramsInteractive);
        chart.setScaleYEnabled(false);
        chart.setDescription(description);
        if(diagramsInteractive){
            chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    onNothingSelected();
                    onDiagramValueSelected(findSample(converter, e).toLatLong());
                }

                @Override
                public void onNothingSelected() {
                    if(highlightingCircle != null){
                        map.getLayerManager().getLayers().remove(highlightingCircle);
                    }
                }
            });
        }
        chart.invalidate();

        converter.afterAdd(chart);

        return chart;
    }

    protected void onDiagramValueSelected(LatLong latLong){
        Paint p= AndroidGraphicFactory.INSTANCE.createPaint();
        p.setColor(0xff693cff);
        highlightingCircle= new FixedPixelCircle(latLong, 20, p, null);
        map.addLayer(highlightingCircle);

        if(!map.getBoundingBox().contains(latLong)){
            map.getModel().mapViewPosition.animateTo(latLong);
        }
    }

    interface SampleConverter{
        void onCreate();
        float getValue(WorkoutSample sample);
        void sampleGetsEntry(WorkoutSample sample, Entry entry);
        String getName();
        String getDescription();
        boolean compare(WorkoutSample sample, Entry entry);
        void afterAdd(LineChart chart);
    }

    WorkoutSample findSample(SampleConverter converter, Entry entry){
        for(WorkoutSample sample : samples){
            if(converter.compare(sample, entry)){
                return sample;
            }
        }
        return null;
    }

    void addHeightDiagram(){
        addDiagram(new SampleConverter() {
            @Override
            public void onCreate() { }

            @Override
            public float getValue(WorkoutSample sample) {
                return (float) UnitUtils.CHOSEN_SYSTEM.getDistanceFromMeters(sample.elevation);
            }

            @Override
            public void sampleGetsEntry(WorkoutSample sample, Entry entry) {
                sample.tmpHeightEntry= entry;
            }

            @Override
            public String getName() {
                return getString(R.string.height);
            }

            @Override
            public String getDescription() {
                return "min - " + UnitUtils.CHOSEN_SYSTEM.getShortDistanceUnit();
            }

            @Override
            public boolean compare(WorkoutSample sample, Entry entry) {
                return sample.tmpHeightEntry.equalTo(entry);
            }

            @Override
            public void afterAdd(LineChart chart) {
                heightDiagram= chart;
            }
        });
    }

    void addSpeedDiagram(){
        addDiagram(new SampleConverter() {
            @Override
            public void onCreate() {
                WorkoutManager.roundSpeedValues(samples);
            }

            @Override
            public float getValue(WorkoutSample sample) {
                return (float)UnitUtils.CHOSEN_SYSTEM.getSpeedFromMeterPerSecond(sample.tmpRoundedSpeed);
            }

            @Override
            public void sampleGetsEntry(WorkoutSample sample, Entry entry) {
                sample.tmpSpeedEntry= entry;
            }

            @Override
            public String getName() {
                return getString(R.string.workoutSpeed);
            }

            @Override
            public String getDescription() {
                return "min - " + UnitUtils.CHOSEN_SYSTEM.getSpeedUnit();
            }

            @Override
            public boolean compare(WorkoutSample sample, Entry entry) {
                return sample.tmpSpeedEntry.equalTo(entry);
            }

            @Override
            public void afterAdd(LineChart chart) {
                speedDiagram= chart;
            }
        });
    }

    protected boolean fullScreenItems = false;
    protected LinearLayout mapRoot;

    void addMap(){
        map= new MapView(this);
        downloadLayer= MapManager.setupMap(map, TileSources.Purpose.DEFAULT);

        WorkoutLayer workoutLayer= new WorkoutLayer(samples, getThemePrimaryColor());
        map.addLayer(workoutLayer);

        final BoundingBox bounds= new BoundingBox(workoutLayer.getLatLongs()).extendMeters(50);
        mHandler.postDelayed(() -> {
            map.getModel().mapViewPosition.setMapPosition(new MapPosition(bounds.getCenterPoint(),
                    (LatLongUtils.zoomForBounds(map.getDimension(), bounds, map.getModel().displayModel.getTileSize()))));
            map.animate().alpha(1f).setDuration(1000).start();
        }, 1000);

        map.getModel().mapViewPosition.setMapLimit(bounds);

        mapRoot= new LinearLayout(this);
        mapRoot.setOrientation(LinearLayout.VERTICAL);
        mapRoot.addView(map);

        root.addView(mapRoot, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fullScreenItems ? ViewGroup.LayoutParams.MATCH_PARENT : getMapHeight()));
        map.setAlpha(0);


        Paint pGreen= AndroidGraphicFactory.INSTANCE.createPaint();
        pGreen.setColor(Color.GREEN);
        map.addLayer(new FixedPixelCircle(samples.get(0).toLatLong(), 20, pGreen, null));
        Paint pRed= AndroidGraphicFactory.INSTANCE.createPaint();
        pRed.setColor(Color.RED);

        map.addLayer(new FixedPixelCircle(samples.get(samples.size()-1).toLatLong(), 20, pRed, null));

        map.setClickable(false);

    }

    int getMapHeight(){
        return getWindowManager().getDefaultDisplay().getWidth()*3/4;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}