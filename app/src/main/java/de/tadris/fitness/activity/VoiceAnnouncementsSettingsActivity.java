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

package de.tadris.fitness.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.NumberPicker;

import de.tadris.fitness.R;
import de.tadris.fitness.util.unit.UnitUtils;

public class VoiceAnnouncementsSettingsActivity extends FitoTrackSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setTitle(R.string.voiceAnnouncementsTitle);

        addPreferencesFromResource(R.xml.preferences_voice_announcements);

        bindPreferenceSummaryToValue(findPreference("announcementMode"));

        findPreference("speechConfig").setOnPreferenceClickListener(preference -> {
            showSpeechConfig();
            return true;
        });

    }

    private void showSpeechConfig() {
        UnitUtils.setUnit(this); // Maybe the user changed unit system

        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        d.setTitle(getString(R.string.pref_voice_announcements_summary));
        View v = getLayoutInflater().inflate(R.layout.dialog_spoken_updates_picker, null);

        NumberPicker npT = v.findViewById(R.id.spokenUpdatesTimePicker);
        npT.setMaxValue(60);
        npT.setMinValue(0);
        npT.setFormatter(value -> value == 0 ? "No speech" : value + " min");
        final String updateTimeVariable = "spokenUpdateTimePeriod";
        npT.setValue(preferences.getInt(updateTimeVariable, 0));
        npT.setWrapSelectorWheel(false);

        final String distanceUnit = " " + UnitUtils.CHOSEN_SYSTEM.getLongDistanceUnit();
        NumberPicker npD = v.findViewById(R.id.spokenUpdatesDistancePicker);
        npD.setMaxValue(10);
        npD.setMinValue(0);
        npD.setFormatter(value -> value == 0 ? "No speech" : value + distanceUnit);
        final String updateDistanceVariable = "spokenUpdateDistancePeriod";
        npD.setValue(preferences.getInt(updateDistanceVariable, 0));
        npD.setWrapSelectorWheel(false);

        d.setView(v);

        d.setNegativeButton(R.string.cancel, null);
        d.setPositiveButton(R.string.okay, (dialog, which) ->
                preferences.edit()
                        .putInt(updateTimeVariable, npT.getValue())
                        .putInt(updateDistanceVariable, npD.getValue())
                        .apply());

        d.create().show();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


}
