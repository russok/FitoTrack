package de.tadris.fitness.recording.goal;

import de.tadris.fitness.R;

public class DistanceGoal extends Goal {

    private int goalDistance;

    public DistanceGoal(int goalDistance) {
        this.goalDistance = goalDistance;
    }

    @Override
    int getName() {
        return R.string.workoutDistance;
    }

    @Override
    double getProgress(long duration, int distance, int calories) {
        return (double) distance / goalDistance;
    }
}
