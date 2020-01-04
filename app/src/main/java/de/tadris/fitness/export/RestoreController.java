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

package de.tadris.fitness.export;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;

public class RestoreController {

    private final Context context;
    private final Uri input;
    private final ImportStatusListener listener;
    private FitoTrackDataContainer dataContainer;
    private final AppDatabase database;

    public RestoreController(Context context, Uri input, ImportStatusListener listener) {
        this.context = context;
        this.input = input;
        this.listener = listener;
        this.database= Instance.getInstance(context).db;
    }

    public void restoreData() throws IOException, UnsupportedVersionException{
        listener.onStatusChanged(0, context.getString(R.string.loadingFile));
        loadDataFromFile();
        checkVersion();
        listener.onStatusChanged(40, context.getString(R.string.preferences));
        restoreSettings();

        restoreDatabase();
        listener.onStatusChanged(100, context.getString(R.string.finished));
    }

    private void loadDataFromFile() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        dataContainer = xmlMapper.readValue(context.getContentResolver().openInputStream(input), FitoTrackDataContainer.class);
    }

    private void checkVersion() throws UnsupportedVersionException {
        if(dataContainer.version != 1){
            throw new UnsupportedVersionException("Version Code" + dataContainer.version + " is unsupported!");
        }
    }

    @SuppressLint("ApplySharedPref")
    private void restoreSettings(){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().clear()
                .putInt("weight", dataContainer.settings.weight)
                .putString("unitSystem", dataContainer.settings.preferredUnitSystem)
                .putBoolean("firstStart", false).putString("mapStyle", dataContainer.settings.mapStyle)
                .commit();
    }

    private void restoreDatabase(){
        database.runInTransaction(() -> {
            resetDatabase();
            restoreWorkouts();
            restoreSamples();
        });
    }

    private void resetDatabase(){
        database.clearAllTables();
    }

    private void restoreWorkouts(){
        listener.onStatusChanged(60, context.getString(R.string.workouts));
        if(dataContainer.workouts != null){
            for(Workout workout : dataContainer.workouts){
                database.workoutDao().insertWorkout(workout);
            }
        }
    }

    private void restoreSamples(){
        listener.onStatusChanged(80, context.getString(R.string.locationData));
        if(dataContainer.samples != null){
            for(WorkoutSample sample : dataContainer.samples){
                database.workoutDao().insertSample(sample);
            }
        }
    }

    public interface ImportStatusListener{
        void onStatusChanged(int progress, String action);
    }

    static class UnsupportedVersionException extends Exception {
        UnsupportedVersionException(String message) {
            super(message);
        }
    }

}
