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

package de.tadris.fitness.announcement;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.annotation.StringRes;

import de.tadris.fitness.recording.WorkoutRecorder;

public abstract class Announcement {

    private Context context;

    Announcement(Context context) {
        this.context = context;
    }

    public boolean isEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("announcement_" + getId(), isEnabledByDefault());
    }

    protected String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    public abstract String getId();

    abstract boolean isEnabledByDefault();

    abstract String getSpoken(WorkoutRecorder recorder);

}
