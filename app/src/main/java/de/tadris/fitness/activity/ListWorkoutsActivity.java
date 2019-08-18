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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.WorkoutAdapter;
import de.tadris.fitness.data.Workout;

public class ListWorkoutsActivity extends Activity implements WorkoutAdapter.WorkoutAdapterListener {

    private RecyclerView listView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    Workout[] workouts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_workouts);

        listView= findViewById(R.id.workoutList);
        listView.setHasFixedSize(true);

        layoutManager= new LinearLayoutManager(this);
        listView.setLayoutManager(layoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();

        workouts= Instance.getInstance(this).db.workoutDao().getWorkouts();
        adapter= new WorkoutAdapter(workouts, this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(Workout workout) {
        ShowWorkoutActivity.selectedWorkout= workout;
        startActivity(new Intent(this, ShowWorkoutActivity.class));
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
        if(id == R.id.action_workout_add){
            startActivity(new Intent(this, RecordWorkoutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}