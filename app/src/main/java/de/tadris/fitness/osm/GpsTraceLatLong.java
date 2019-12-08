package de.tadris.fitness.osm;

import de.tadris.fitness.data.WorkoutSample;
import de.westnordost.osmapi.map.data.LatLon;

public class GpsTraceLatLong implements LatLon {

    private final double latitude;
    private final double longitude;

    public GpsTraceLatLong(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public GpsTraceLatLong(WorkoutSample sample) {
        this(sample.lat, sample.lon);
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
