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

package de.tadris.fitness.util;

import android.app.AlertDialog;
import android.content.Context;

import de.tadris.fitness.R;

public class DialogUtils {

    public static void showDeleteWorkoutDialog(Context context, WorkoutDeleter deleter){
        new AlertDialog.Builder(context).setTitle(R.string.deleteWorkout)
                .setMessage(R.string.deleteWorkoutMessage)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleter.deleteWorkout())
                .create().show();
    }

    public interface WorkoutDeleter{
        void deleteWorkout();
    }

}
