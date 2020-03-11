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

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutType;
import de.tadris.fitness.util.DialogUtils;
import de.tadris.fitness.view.WorkoutAdapter;

public class ListWorkoutsActivity extends FitoTrackActivity implements WorkoutAdapter.WorkoutAdapterListener {

    private RecyclerView listView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionMenu menu;
    private Workout[] workouts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_workouts);

        listView= findViewById(R.id.workoutList);
        listView.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

        menu= findViewById(R.id.workoutListMenu);
        menu.setOnMenuButtonLongClickListener(v -> {
            if(workouts.length > 0){
                startRecording(workouts[0].getWorkoutType());
                return true;
            }else{
                return false;
            }
        });

        findViewById(R.id.workoutListRecordRunning).setOnClickListener(v -> startRecording(WorkoutType.RUNNING));
        findViewById(R.id.workoutListRecordHiking).setOnClickListener(v -> startRecording(WorkoutType.HIKING));
        findViewById(R.id.workoutListRecordCycling).setOnClickListener(v -> startRecording(WorkoutType.CYCLING));
        findViewById(R.id.workoutListEnter).setOnClickListener(v -> startEnterWorkoutActivity());

        checkFirstStart();

        refresh();
    }

    private void checkFirstStart(){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences.getBoolean("firstStart", true)){
            preferences.edit().putBoolean("firstStart", false).apply();
            new AlertDialog.Builder(this)
                    .setTitle(R.string.setPreferencesTitle)
                    .setMessage(R.string.setPreferencesMessage)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.settings, (dialog, which) -> startActivity(new Intent(ListWorkoutsActivity.this, SettingsActivity.class)))
                    .create().show();
        }
    }

    private void startEnterWorkoutActivity() {
        menu.close(true);
        final Intent intent = new Intent(this, EnterWorkoutActivity.class);
        new Handler().postDelayed(() -> startActivity(intent), 300);
    }

    private void startRecording(WorkoutType activity) {
        menu.close(true);
        RecordWorkoutActivity.ACTIVITY= activity;
        final Intent intent= new Intent(this, RecordWorkoutActivity.class);
        new Handler().postDelayed(() -> startActivity(intent), 300);
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public void onItemClick(int pos, Workout workout) {
        ShowWorkoutActivity.selectedWorkout= workout;
        startActivity(new Intent(this, ShowWorkoutActivity.class));
    }

    @Override
    public void onItemLongClick(int pos, Workout workout) {
        DialogUtils.showDeleteWorkoutDialog(this, () -> {
            Instance.getInstance(ListWorkoutsActivity.this).db.workoutDao().deleteWorkout(workout);
            refresh();
        });
    }

    private void refresh() {
        loadData();
        refreshAdapter();
    }

    private void loadData(){
        workouts= Instance.getInstance(this).db.workoutDao().getWorkouts();
    }

    private void refreshAdapter(){
        adapter= new WorkoutAdapter(workouts, this);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_workout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionOpenSettings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
