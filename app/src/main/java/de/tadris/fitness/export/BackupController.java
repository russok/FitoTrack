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

import android.content.Context;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.tadris.fitness.Instance;
import de.tadris.fitness.R;
import de.tadris.fitness.data.AppDatabase;
import de.tadris.fitness.util.unit.UnitUtils;

public class BackupController {

    private static final int VERSION = 1;

    private final Context context;
    private final File output;
    private final ExportStatusListener listener;
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
        listener.onStatusChanged(20, context.getString(R.string.workouts));
        saveWorkoutsToContainer();
        listener.onStatusChanged(40, context.getString(R.string.locationData));
        saveSamplesToContainer();
        listener.onStatusChanged(60, context.getString(R.string.converting));
        writeContainerToOutputFile();
        listener.onStatusChanged(100, context.getString(R.string.finished));
    }

    private void init(){
        database= Instance.getInstance(context).db;
        UnitUtils.setUnit(context); // Ensure unit system is correct
        newContainer();
    }

    private void newContainer(){
        dataContainer= new FitoTrackDataContainer();
        dataContainer.setVersion(VERSION);
        dataContainer.setWorkouts(new ArrayList<>());
        dataContainer.setSamples(new ArrayList<>());
    }

    private void saveWorkoutsToContainer(){
        dataContainer.getWorkouts().addAll(Arrays.asList(database.workoutDao().getWorkouts()));
    }

    private void saveSamplesToContainer(){
        dataContainer.getSamples().addAll(Arrays.asList(database.workoutDao().getSamples()));
    }

    private void writeContainerToOutputFile() throws IOException {
        XmlMapper mapper= new XmlMapper();
        mapper.writeValue(output, dataContainer);
    }

    public interface ExportStatusListener{
        void onStatusChanged(int progress, String action);
    }

}
