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
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.util.gpx.GpxExporter;
import de.tadris.fitness.util.unit.UnitUtils;
import de.tadris.fitness.view.ProgressDialogController;

public class ShowWorkoutActivity extends WorkoutActivity {

    TableLayout performanceTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initBeforeContent();

        setContentView(R.layout.activity_show_workout);
        root= findViewById(R.id.showWorkoutRoot);

        initAfterContent();

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

        map.setClickable(false);
        mapRoot.setOnClickListener(v -> startActivity(new Intent(ShowWorkoutActivity.this, ShowWorkoutMapActivity.class)));

        addTitle(getString(R.string.workoutSpeed));

        addKeyValue(getString(R.string.workoutAvgSpeed), UnitUtils.getSpeed(workout.avgSpeed),
                getString(R.string.workoutTopSpeed), UnitUtils.getSpeed(workout.topSpeed));

        addSpeedDiagram();

        speedDiagram.setOnClickListener(v -> startDiagramActivity(ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE_SPEED));

        addTitle(getString(R.string.workoutBurnedEnergy));
        addKeyValue(getString(R.string.workoutTotalEnergy), workout.calorie + " kcal",
                getString(R.string.workoutEnergyConsumption), UnitUtils.getRelativeEnergyConsumption((double)workout.calorie / ((double)workout.length / 1000)));

        addTitle(getString(R.string.height));

        addKeyValue(getString(R.string.workoutAscent), UnitUtils.getDistance((int)workout.ascent),
                getString(R.string.workoutDescent), UnitUtils.getDistance((int)workout.descent));

        addHeightDiagram();

        heightDiagram.setOnClickListener(v -> startDiagramActivity(ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE_HEIGHT));


    }

    void startDiagramActivity(String diagramType){
        ShowWorkoutMapDiagramActivity.DIAGRAM_TYPE= diagramType;
        startActivity(new Intent(ShowWorkoutActivity.this, ShowWorkoutMapDiagramActivity.class));
    }


    void openEditCommentDialog(final TextView change){
        final EditText editText= new EditText(this);
        editText.setText(workout.comment);
        editText.setSingleLine(true);
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

    void addPerformanceTable(){
        performanceTable= new TableLayout(this);

        addTitle("Performance"); // TODO: localization
        root.addView(performanceTable);

        refreshPerformanceTable();
    }

    void refreshPerformanceTable(){

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

    private void exportToGpx(){
        ProgressDialogController dialogController= new ProgressDialogController(this, getString(R.string.exporting));
        dialogController.setIndeterminate(true);
        dialogController.show();
        new Thread(() -> {
            try{
                String file= getFilesDir().getAbsolutePath() + "/shared/workout.gpx";
                new File(file).getParentFile().mkdirs();
                Uri uri= FileProvider.getUriForFile(getBaseContext(), "de.tadris.fitness.fileprovider", new File(file));

                GpxExporter.exportWorkout(getBaseContext(), workout, new File(file));
                dialogController.cancel();
                mHandler.post(() -> shareFile(uri));
            }catch (Exception e){
                mHandler.post(() -> showErrorDialog(e, R.string.error, R.string.errorGpxExportFailed));
            }
        }).start();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.actionDeleteWorkout:
                showDeleteDialog();
                return true;
            case R.id.actionExportGpx:
                exportToGpx();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
