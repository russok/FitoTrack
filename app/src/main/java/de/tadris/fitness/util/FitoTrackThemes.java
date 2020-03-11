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

package de.tadris.fitness.util;

import android.content.Context;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import androidx.annotation.StyleRes;

import de.tadris.fitness.R;
import de.tadris.fitness.data.WorkoutType;

public class FitoTrackThemes {

    private static final int THEME_SETTING_AUTO = 0;
    private static final int THEME_SETTING_LIGHT = 1;
    private static final int THEME_SETTING_DARK = 2;

    private Context context;

    public FitoTrackThemes(Context context) {
        this.context = context;
    }

    @StyleRes
    public int getDefaultTheme() {
        if (shouldUseLightMode()) {
            return R.style.AppTheme;
        } else {
            return R.style.AppThemeDark;
        }
    }

    @StyleRes
    public int getWorkoutTypeTheme(WorkoutType type) {
        if (shouldUseLightMode()) {
            return type.lightTheme;
        } else {
            return type.darkTheme;
        }
    }

    private boolean shouldUseLightMode() {
        switch (getThemeSetting()) {
            default:
            case THEME_SETTING_AUTO:
                return !isSystemNightModeEnabled();
            case THEME_SETTING_LIGHT:
                return true;
            case THEME_SETTING_DARK:
                return false;
        }
    }

    private int getThemeSetting() {
        String setting = PreferenceManager.getDefaultSharedPreferences(context).getString("themeSetting", String.valueOf(THEME_SETTING_AUTO));
        assert setting != null;
        return Integer.parseInt(setting);
    }

    private boolean isSystemNightModeEnabled() {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

}
