package de.tadris.fitness.recording.goal;

import androidx.annotation.StringRes;

abstract public class Goal {

    @StringRes
    abstract int getName();

    abstract double getProgress(long duration, int distance, int calories);

}
