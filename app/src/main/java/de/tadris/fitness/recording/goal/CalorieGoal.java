package de.tadris.fitness.recording.goal;

import de.tadris.fitness.R;

public class CalorieGoal extends Goal {

    private int calories;

    public CalorieGoal(int calories) {
        this.calories = calories;
    }

    @Override
    int getName() {
        return R.string.calories;
    }

    @Override
    double getProgress(long duration, int distance, int calories) {
        return (double) calories / this.calories;
    }
}
