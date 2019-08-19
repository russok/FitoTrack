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

import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.location.LocationListener;

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
    public UserPreferences userPreferences;
    public List<LocationListener.LocationChangeListener> locationChangeListeners= new ArrayList<>();

    private Instance(Context context) {
        db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
        userPreferences= new UserPreferences();
    }
}
