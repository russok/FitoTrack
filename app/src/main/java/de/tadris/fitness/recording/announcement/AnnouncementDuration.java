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

package de.tadris.fitness.recording.announcement;

import android.content.Context;

import de.tadris.fitness.R;
import de.tadris.fitness.recording.WorkoutRecorder;

public class AnnouncementDuration extends Announcement {

    public AnnouncementDuration(Context context) {
        super(context);
    }

    @Override
    public String getId() {
        return "duration";
    }

    @Override
    boolean isEnabledByDefault() {
        return true;
    }

    @Override
    String getSpoken(WorkoutRecorder recorder) {
        return getString(R.string.workoutDuration) + ": " + getSpokenTime(recorder.getDuration()) + ".";
    }

    private String getSpokenTime(long duration) {
        final long minute = 1000L * 60;
        final long hour = minute * 60;

        StringBuilder spokenTime = new StringBuilder();

        if (duration > hour) {
            long hours = duration / hour;
            duration = duration % hour; // Set duration to the rest
            spokenTime.append(hours).append(" ");
            spokenTime.append(getString(hours == 1 ? R.string.timeHourSingular : R.string.timeHourPlural)).append(" ")
                    .append(getString(R.string.and)).append(" ");
        }
        long minutes = duration / minute;
        spokenTime.append(minutes).append(" ");
        spokenTime.append(getString(minutes == 1 ? R.string.timeMinuteSingular : R.string.timeMinutePlural));

        return spokenTime.toString();
    }
}
