package de.tadris.fitness.util.export;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

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
        container.workouts.addAll(Arrays.asList(database.workoutDao().getWorkouts()));

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