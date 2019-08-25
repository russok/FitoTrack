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

package de.tadris.fitness;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.data.UserPreferences;
import de.tadris.fitness.recording.LocationListener;
import de.tadris.fitness.util.unit.UnitUtils;

public class Instance {

    public static final String DATABASE_NAME= "fito-track";

    private static Instance instance;

    public static Instance getInstance(Context context){
        if(instance == null){
            instance= new Instance(context);
        }
        return instance;
    }

    public AppDatabase db;
    public List<LocationListener.LocationChangeListener> locationChangeListeners= new ArrayList<>();
    public UserPreferences userPreferences;

    public boolean pressureAvailable= false;
    public float lastPressure= 0;

    private Instance(Context context) {
        userPreferences= new UserPreferences(context);
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .addMigrations(new Migration(1, 2) {
                    @Override
                    public void migrate(@NonNull SupportSQLiteDatabase database) {
                        try{
                            database.beginTransaction();

                            database.execSQL("ALTER table workout add descent REAL NOT NULL DEFAULT 0;");
                            database.execSQL("ALTER table workout add ascent REAL NOT NULL DEFAULT 0");

                            database.execSQL("ALTER TABLE workout_sample RENAME TO workout_sample2;");

                            database.execSQL("CREATE TABLE workout_sample (" +
                                    "id INTEGER NOT NULL DEFAULT NULL PRIMARY KEY," +
                                    "relativeTime INTEGER NOT NULL DEFAULT NULL," +
                                    "elevation REAL NOT NULL DEFAULT NULL," +
                                    "absoluteTime INTEGER NOT NULL DEFAULT NULL," +
                                    "lat REAL NOT NULL DEFAULT NULL," +
                                    "lon REAL NOT NULL DEFAULT NULL," +
                                    "speed REAL NOT NULL DEFAULT NULL," +
                                    "workout_id INTEGER NOT NULL DEFAULT NULL," +
                                    "FOREIGN KEY (workout_id) REFERENCES workout(id) ON DELETE CASCADE);");

                            database.execSQL("INSERT INTO workout_sample (id, relativeTime, elevation, absoluteTime, lat, lon, speed, workout_id) " +
                                    "SELECT id, relativeTime, elevation, absoluteTime, lat, lon, speed, workout_id FROM workout_sample2");

                            database.execSQL("DROP TABLE workout_sample2");

                            database.setTransactionSuccessful();
                        }finally {
                            database.endTransaction();
                        }
                    }
                })
                .allowMainThreadQueries()
                .build();
        UnitUtils.setUnit(context);
    }
}
