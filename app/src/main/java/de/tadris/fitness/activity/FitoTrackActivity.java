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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.StringRes;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

import de.tadris.fitness.R;

abstract public class FitoTrackActivity extends Activity {


    int getThemePrimaryColor() {
        final TypedValue value = new TypedValue ();
        getTheme().resolveAttribute (android.R.attr.colorPrimary, value, true);
        return value.data;
    }

    void shareFile(Uri uri) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setDataAndType(uri, getContentResolver().getType(uri));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intentShareFile, getString(R.string.shareFile)));

        Log.d("Export", uri.toString());
        Log.d("Export", getContentResolver().getType(uri));
        try {
            Log.d("Export", new BufferedInputStream(getContentResolver().openInputStream(uri)).toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void showErrorDialog(Exception e, @StringRes int title, @StringRes int message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(getString(message) + "\n\n" + e.getMessage())
                .setPositiveButton(R.string.okay, null)
                .create().show();
    }


}
