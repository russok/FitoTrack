package de.tadris.fitness;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.room.Room;

import de.tadris.fitness.data.AppDatabase;

public class ListWorkoutsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_workouts);


    }
}
