package de.tadris.fitness.util.unit;

public class ImperialWithMeters extends Imperial {

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public double getDistanceFromMeters(double meters) {
        return meters;
    }
    @Override
    public String getLongDistanceUnit() {
        return "m";
    }

    @Override
    public String getShortDistanceUnit() {
        return "yd";
    }
}
