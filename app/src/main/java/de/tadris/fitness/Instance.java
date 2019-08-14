package de.tadris.fitness;

import android.content.Context;

import androidx.room.Room;

import de.tadris.fitness.data.AppDatabase;

public class Instance {

    public static final String DATABASE_NAME= "fito-track";

    private static Instance instance;

    public Instance getInstance(Context context){
        if(instance == null){
            instance= new Instance(context);
        }
        return instance;
    }

    public AppDatabase db;

    private Instance(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME).build();
    }
}
