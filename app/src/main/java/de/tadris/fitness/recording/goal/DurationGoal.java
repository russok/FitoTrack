package de.tadris.fitness.recording.goal;

import de.tadris.fitness.R;

public class DurationGoal extends Goal {

    private long duration;

    @Override
    int getName() {
        return R.string.workoutDuration;
    }

    public DurationGoal(long duration) {
        this.duration = duration;
    }

    @Override
    double getProgress(long duration, int distance, int calories) {
        return (double) duration / this.duration;
    }
}
