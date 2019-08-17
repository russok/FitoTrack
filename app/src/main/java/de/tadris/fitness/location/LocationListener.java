/*
 * Copyright (c) 2019 Jannis Scheibe <jannis@tadris.de>
 *
 * This file is part of FitoTrack
 *
 * FitoTrack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FitoTrack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.tadris.fitness.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;

import org.mapsforge.core.model.LatLong;

import java.util.ArrayList;
import java.util.List;

public class LocationListener implements android.location.LocationListener {

    public static LatLong static_lastLocation;

    /**
     * @param location the location whose geographical coordinates should be converted.
     * @return a new LatLong with the geographical coordinates taken from the given location.
     */
    public static LatLong locationToLatLong(Location location) {
        return new LatLong(location.getLatitude(), location.getLongitude());
    }

    private Context activity;
    private Location lastLocation;
    private final LocationManager locationManager;
    private boolean myLocationEnabled;

    public LocationListener(Context context) {
        super();
        this.activity= context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * Stops the receiving of location updates. Has no effect if location updates are already disabled.
     */
    public synchronized void disableMyLocation() {
        if (this.myLocationEnabled) {
            this.myLocationEnabled = false;
            try {
                this.locationManager.removeUpdates(this);
            } catch (RuntimeException runtimeException) {
                // do we need to catch security exceptions for this call on Android 6?
            }
        }
    }

    public synchronized void enableMyLocation() {
        enableBestAvailableProvider();
    }

    /**
     * @return the most-recently received location fix (might be null).
     */
    public synchronized Location getLastLocation() {
        return this.lastLocation;
    }

    /**
     * @return true if the receiving of location updates is currently enabled, false otherwise.
     */
    public synchronized boolean isMyLocationEnabled() {
        return this.myLocationEnabled;
    }

    @Override
    public void onLocationChanged(Location location) {

        synchronized (this) {
            this.lastLocation = location;

            LatLong latLong = locationToLatLong(location);
            static_lastLocation= latLong;

            for(LocationChangeListener listener : this.locationChangeListeners){
                listener.onLocationChange(location);
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        enableBestAvailableProvider();
    }

    @Override
    public void onProviderEnabled(String provider) {
        enableBestAvailableProvider();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // do nothing
    }

    private void enableBestAvailableProvider() {
        disableMyLocation();

        boolean result = false;
        for (String provider : this.locationManager.getProviders(true)) {
            if (LocationManager.GPS_PROVIDER.equals(provider) || LocationManager.NETWORK_PROVIDER.equals(provider)) {
                result = true;
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                this.locationManager.requestLocationUpdates(provider, 0, 0, this);
                Location location= this.locationManager.getLastKnownLocation(provider);
                if(location != null){
                    onLocationChanged(location);
                }
            }
        }
        this.myLocationEnabled = result;
    }

    private List<LocationChangeListener> locationChangeListeners= new ArrayList<>();

    public void registerLocationChangeListeners(LocationChangeListener listener){
        if(locationChangeListeners.size() == 0){
            enableMyLocation();
        }
        locationChangeListeners.add(listener);
    }

    public void unregisterLocationChangeListeners(LocationChangeListener listener){
        locationChangeListeners.remove(listener);
        if(locationChangeListeners.size() == 0){
            disableMyLocation();
        }
    }

    public interface LocationChangeListener{
        void onLocationChange(Location location);
    }

}
