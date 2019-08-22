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

package de.tadris.fitness.util.export;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.data.UserPreferences;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;
import de.tadris.fitness.util.unit.UnitUtils;

public class Exporter {

    public static final int VERSION= 1;

    public static void exportData(Context context, File output, ExportStatusListener listener) throws IOException {
        listener.onStatusChanged(0, context.getString(R.string.initialising));
        UserPreferences preferences= Instance.getInstance(context).userPreferences;
        AppDatabase database= Instance.getInstance(context).db;
        UnitUtils.setUnit(context);

        FitoTrackDataContainer container= new FitoTrackDataContainer();
        container.version= VERSION;
        container.workouts= new ArrayList<>();
        container.samples= new ArrayList<>();

        listener.onStatusChanged(10, context.getString(R.string.preferences));
        FitoTrackSettings settings= new FitoTrackSettings();
        settings.weight= preferences.getUserWeight();
        settings.preferredUnitSystem= String.valueOf(UnitUtils.CHOSEN_SYSTEM.getId());
        container.settings= settings;

        listener.onStatusChanged(20, context.getString(R.string.workouts));
        container.workouts.addAll(Arrays.asList(database.workoutDao().getWorkouts()));
        listener.onStatusChanged(40, context.getString(R.string.locationData));
        container.samples.addAll(Arrays.asList(database.workoutDao().getSamples()));

        listener.onStatusChanged(60, context.getString(R.string.converting));

        XmlMapper mapper= new XmlMapper();
        mapper.writeValue(output, container);

        listener.onStatusChanged(100, context.getString(R.string.finished));
    }

    @SuppressLint("ApplySharedPref")
    public static void importData(Context context, Uri input, ExportStatusListener listener) throws IOException{
        listener.onStatusChanged(0, context.getString(R.string.loadingFile));
        XmlMapper xmlMapper = new XmlMapper();
        FitoTrackDataContainer container = xmlMapper.readValue(context.getContentResolver().openInputStream(input), FitoTrackDataContainer.class);
        xmlMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);

        if(container.version != 1){
            throw new UnsupportedEncodingException("Version Code" + container.version + " is unsupported!");
        }

        listener.onStatusChanged(40, context.getString(R.string.preferences));
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().clear()
                .putInt("weight", container.settings.weight)
                .putString("unitSystem", container.settings.preferredUnitSystem)
                .commit();

        AppDatabase database= Instance.getInstance(context).db;
        database.clearAllTables();

        listener.onStatusChanged(60, context.getString(R.string.workouts));
        if(container.workouts != null){
            for(Workout workout : container.workouts){
                database.workoutDao().insertWorkout(workout);
            }
        }

        listener.onStatusChanged(80, context.getString(R.string.locationData));
        if(container.samples != null){
            for(WorkoutSample sample : container.samples){
                database.workoutDao().insertSample(sample);
            }
        }

        listener.onStatusChanged(100, context.getString(R.string.finished));
    }


    public interface ExportStatusListener{
        void onStatusChanged(int progress, String action);
    }

    public static class UnsupportedVersionException extends Exception{
        public UnsupportedVersionException(String message) {
            super(message);
        }
    }

}
