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
