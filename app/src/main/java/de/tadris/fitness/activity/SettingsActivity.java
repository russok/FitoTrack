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

package de.tadris.fitness.activity;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.StringRes;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import de.tadris.fitness.R;
import de.tadris.fitness.util.export.Exporter;
import de.tadris.fitness.util.unit.UnitUtils;
import de.tadris.fitness.view.ProgressDialogController;

public class SettingsActivity extends PreferenceActivity {


    protected void shareFile(Uri uri){
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

        }
    }

    protected void showErrorDialog(Exception e, @StringRes int title, @StringRes int message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(getString(message) + "\n\n" + e.getMessage())
                .setPositiveButton(R.string.okay, null)
                .create().show();
    }



    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = (preference, value) -> {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else if (preference instanceof RingtonePreference) {
            // For ringtone preferences, look up the correct display value
            // using RingtoneManager.
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to 'silent' (no ringtone).
                preference.setSummary(R.string.pref_ringtone_silent);

            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(
                        preference.getContext(), Uri.parse(stringValue));

                if (ringtone == null) {
                    // Clear the summary if there was a lookup error.
                    preference.setSummary(null);
                } else {
                    // Set the summary to reflect the new ringtone display
                    // name.
                    String name = ringtone.getTitle(preference.getContext());
                    preference.setSummary(name);
                }
            }

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private Handler mHandler= new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setTitle(R.string.settings);

        addPreferencesFromResource(R.xml.preferences_main);

        bindPreferenceSummaryToValue(findPreference("unitSystem"));
        bindPreferenceSummaryToValue(findPreference("mapStyle"));

        findPreference("weight").setOnPreferenceClickListener(preference -> showWeightPicker());
        findPreference("import").setOnPreferenceClickListener(preference -> showImportDialog());
        findPreference("export").setOnPreferenceClickListener(preference -> showExportDialog());

    }

    private boolean showExportDialog(){
        new AlertDialog.Builder(this)
                .setTitle(R.string.exportData)
                .setMessage(R.string.exportDataSummary)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.backup, (dialog, which) -> {
                    exportBackup();
                }).create().show();
        return true;
    }

    private void exportBackup(){
        ProgressDialogController dialogController= new ProgressDialogController(this, getString(R.string.backup));
        dialogController.show();
        new Thread(() -> {
            try{
                String file= getFilesDir().getAbsolutePath() + "/shared/backup.ftb";
                new File(file).getParentFile().mkdirs();
                Uri uri= FileProvider.getUriForFile(getBaseContext(), "de.tadris.fitness.fileprovider", new File(file));

                Exporter.exportData(getBaseContext(), new File(file),
                        (progress, action) -> mHandler.post(() -> dialogController.setProgress(progress, action)));

                mHandler.post(() -> {
                    dialogController.cancel();
                    shareFile(uri);
                });
            }catch (Exception e){
                e.printStackTrace();
                mHandler.post(() -> {
                    dialogController.cancel();
                    showErrorDialog(e, R.string.error, R.string.errorExportFailed);
                });
            }
        }).start();
    }

    private boolean showImportDialog(){
        if(!hasPermission()){
            requestPermissions();
            return true;
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.importBackup)
                .setMessage(R.string.importBackupMessage)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.restore, (dialog, which) -> {
                    importBackup();
                }).create().show();
        return true;
    }

    void requestPermissions(){
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
        }
    }

    public boolean hasPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private static final int FILE_SELECT_CODE= 21;
    private void importBackup(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, getString(R.string.chooseBackupFile)), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ignored) { }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK){
                    importBackup(data.getData());
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importBackup(Uri uri){
        ProgressDialogController dialogController= new ProgressDialogController(this, getString(R.string.backup));
        dialogController.show();
        new Thread(() -> {
            try{
                Exporter.importData(getBaseContext(), uri,
                        (progress, action) -> mHandler.post(() -> dialogController.setProgress(progress, action)));

                // DO on backup finished
                mHandler.post(dialogController::cancel);
            }catch (Exception e){
                e.printStackTrace();
                mHandler.post(() -> {
                    dialogController.cancel();
                    showErrorDialog(e, R.string.error, R.string.errorImportFailed);
                });
            }
        }).start();
    }

    private boolean showWeightPicker() {
        final AlertDialog.Builder d = new AlertDialog.Builder(this);
        final SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        d.setTitle(getString(R.string.pref_weight));
        View v= getLayoutInflater().inflate(R.layout.dialog_weight_picker, null);
        NumberPicker np = v.findViewById(R.id.weightPicker);
        np.setMaxValue((int) UnitUtils.CHOSEN_SYSTEM.getWeightFromKilogram(150));
        np.setMinValue((int) UnitUtils.CHOSEN_SYSTEM.getWeightFromKilogram(20));
        np.setFormatter(value -> value + " " + UnitUtils.CHOSEN_SYSTEM.getWeightUnit());
        np.setValue(preferences.getInt("weight", 80));
        np.setWrapSelectorWheel(false);

        d.setView(v);

        d.setNegativeButton(R.string.cancel, null);
        d.setPositiveButton(R.string.okay, (dialog, which) -> {
            int unitValue= np.getValue();
            int kilograms= (int)Math.round(UnitUtils.CHOSEN_SYSTEM.getKilogramFromUnit(unitValue));
            preferences.edit().putInt("weight", kilograms).apply();
        });

        d.create().show();

        return true;
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnitUtils.setUnit(this);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
