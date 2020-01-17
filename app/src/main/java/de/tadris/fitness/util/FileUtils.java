package de.tadris.fitness.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tadris.fitness.R;

public class FileUtils {

    public static void saveOrShareFile(Activity activity, Uri uri, String suffix) {
        String[] colors = {activity.getString(R.string.share), activity.getString(R.string.save)};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setItems(colors, (dialog, which) -> {
            if (which == 0) {
                shareFile(activity, uri);
            } else {
                try {
                    saveFile(activity, uri, suffix);
                    Toast.makeText(activity, R.string.savedToDownloads, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(activity, R.string.savingFailed, Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.show();
    }

    private static void saveFile(Activity activity, Uri fileUri, String suffix) throws IOException {
        File target = new File(Environment.getExternalStorageDirectory(), "Download/fitotrack" + System.currentTimeMillis() + "." + suffix);
        if (!target.createNewFile()) {
            throw new IOException("Cannot write to file " + target);
        }
        copyFile(activity, fileUri, Uri.fromFile(target));
    }

    private static void copyFile(Activity activity, Uri sourceUri, Uri targetUri) throws IOException {
        InputStream input = activity.getContentResolver().openInputStream(sourceUri);
        if (input == null) {
            throw new IOException("Source file not found");
        }
        OutputStream output = activity.getContentResolver().openOutputStream(targetUri);
        IOUtils.copy(input, output);
    }

    private static void shareFile(Activity activity, Uri uri) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        intentShareFile.setDataAndType(uri, activity.getContentResolver().getType(uri));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(Intent.createChooser(intentShareFile, activity.getString(R.string.shareFile)));

        Log.d("Export", uri.toString());
        Log.d("Export", activity.getContentResolver().getType(uri));
        try {
            Log.d("Export", new BufferedInputStream(activity.getContentResolver().openInputStream(uri)).toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
