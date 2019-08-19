package de.tadris.fitness.util.unit;

public class MetricPhysical extends Metric{

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public double getSpeedFromMeterPerSecond(double meterPerSecond) {
        return meterPerSecond;
    }

    @Override
    public String getSpeedUnit() {
        return "m/s";
    }
}
