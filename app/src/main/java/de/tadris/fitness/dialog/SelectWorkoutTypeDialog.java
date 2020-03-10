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

package de.tadris.fitness.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;

import de.tadris.fitness.R;
import de.tadris.fitness.data.WorkoutType;

public class SelectWorkoutTypeDialog {

    private Activity context;
    private WorkoutTypeSelectListener listener;
    private WorkoutType[] options;

    public SelectWorkoutTypeDialog(Activity context, WorkoutTypeSelectListener listener) {
        this.context = context;
        this.listener = listener;
        this.options = WorkoutType.values();
    }

    public void show() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.select_dialog_singlechoice_material);
        for (WorkoutType type : options) {
            arrayAdapter.add(context.getString(type.title));
        }

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> listener.onSelectWorkoutType(options[which]));
        builderSingle.show();
    }

    public interface WorkoutTypeSelectListener {
        void onSelectWorkoutType(WorkoutType workoutType);
    }
}
