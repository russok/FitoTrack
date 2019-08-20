package de.tadris.fitness.util.gpx;

public class TrackPointExtension {

    double speed;

    public TrackPointExtension(){}

    public TrackPointExtension(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
