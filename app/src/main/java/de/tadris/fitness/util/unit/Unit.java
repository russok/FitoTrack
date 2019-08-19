package de.tadris.fitness.util.unit;

public interface Unit {

    int getId();
    double getDistanceFromMeters(double meters);
    double getDistanceFromKilometers(double kilometers);
    double getWeightFromKilogram(double kilogram);
    double getKilogramFromUnit(double unit);
    double getSpeedFromMeterPerSecond(double meterPerSecond);
    String getLongDistanceUnit();
    String getShortDistanceUnit();
    String getWeightUnit();
    String getSpeedUnit();

}
