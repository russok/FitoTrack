package de.tadris.fitness.util.unit;

public class Metric implements Unit{

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public double getDistanceFromMeters(double meters) {
        return meters;
    }

    @Override
    public double getDistanceFromKilometers(double kilometers) {
        return kilometers;
    }

    @Override
    public double getWeightFromKilogram(double kilogram) {
        return kilogram;
    }

    @Override
    public double getKilogramFromUnit(double unit) {
        return unit;
    }

    @Override
    public double getSpeedFromMeterPerSecond(double meterPerSecond) {
        return meterPerSecond * 3.6;
    }

    @Override
    public String getLongDistanceUnit() {
        return "km";
    }

    @Override
    public String getShortDistanceUnit() {
        return "m";
    }

    @Override
    public String getWeightUnit() {
        return "kg";
    }

    @Override
    public String getSpeedUnit() {
        return "km/h";
    }
}
