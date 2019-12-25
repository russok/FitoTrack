package de.tadris.fitness.export;

import android.content.Context;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.data.UserPreferences;
import de.tadris.fitness.util.unit.UnitUtils;

public class BackupController {

    public static final int VERSION= 1;

    private Context context;
    private File output;
    private ExportStatusListener listener;
    private UserPreferences preferences;
    private AppDatabase database;

    private FitoTrackDataContainer dataContainer;

    public BackupController(Context context, File output, ExportStatusListener listener) {
        this.context = context;
        this.output = output;
        this.listener = listener;
    }

    public void exportData() throws IOException {
        listener.onStatusChanged(0, context.getString(R.string.initialising));
        init();
        listener.onStatusChanged(10, context.getString(R.string.preferences));
        newContainer();

        saveSettingsToContainer();
        listener.onStatusChanged(20, context.getString(R.string.workouts));
        saveWorkoutsToContainer();
        listener.onStatusChanged(40, context.getString(R.string.locationData));
        saveSamplesToContainer();
        listener.onStatusChanged(60, context.getString(R.string.converting));
        writeContainerToOutputFile();
        listener.onStatusChanged(100, context.getString(R.string.finished));
    }

    private void init(){
        preferences= Instance.getInstance(context).userPreferences;
        database= Instance.getInstance(context).db;
        UnitUtils.setUnit(context); // Ensure unit system is correct
    }

    private void newContainer(){
        dataContainer= new FitoTrackDataContainer();
        dataContainer.version= VERSION;
        dataContainer.workouts= new ArrayList<>();
        dataContainer.samples= new ArrayList<>();
    }

    private void saveSettingsToContainer(){
        FitoTrackSettings settings= new FitoTrackSettings();
        settings.weight= preferences.getUserWeight();
        settings.mapStyle= preferences.getMapStyle();
        settings.preferredUnitSystem= String.valueOf(UnitUtils.CHOSEN_SYSTEM.getId());
        dataContainer.settings= settings;
    }

    private void saveWorkoutsToContainer(){
        dataContainer.workouts.addAll(Arrays.asList(database.workoutDao().getWorkouts()));
    }

    private void saveSamplesToContainer(){
        dataContainer.samples.addAll(Arrays.asList(database.workoutDao().getSamples()));
    }

    private void writeContainerToOutputFile() throws IOException {
        XmlMapper mapper= new XmlMapper();
        mapper.writeValue(output, dataContainer);
    }

    public interface ExportStatusListener{
        void onStatusChanged(int progress, String action);
    }

}
