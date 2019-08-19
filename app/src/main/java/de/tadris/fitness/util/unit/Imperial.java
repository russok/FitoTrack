package de.tadris.fitness.util.unit;

public class Imperial implements Unit {

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public double getDistanceFromMeters(double meters) {
        return meters * 1.093613d;
    }

    @Override
    public double getDistanceFromKilometers(double kilometers) {
        return kilometers * 0.62137d;
    }

    @Override
    public double getWeightFromKilogram(double kilogram) {
        return kilogram * 2.2046;
    }

    @Override
    public double getKilogramFromUnit(double unit) {
        return unit / 2.2046;
    }

    @Override
    public double getSpeedFromMeterPerSecond(double meterPerSecond) {
        return meterPerSecond*3.6*0.62137d;
    }

    @Override
    public String getLongDistanceUnit() {
        return "mi";
    }

    @Override
    public String getShortDistanceUnit() {
        return "yd";
    }

    @Override
    public String getWeightUnit() {
        return "lbs";
    }

    @Override
    public String getSpeedUnit() {
        return "mi/h";
    }
}
