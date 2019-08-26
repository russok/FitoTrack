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

package de.tadris.fitness.recording;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import de.tadris.fitness.Instance;

public class PressureService extends Service {

    private static final String TAG = "PressureService";

    private SensorManager sensorManager;
    private Instance instance;
    private Sensor pressureSensor;
    private PressureListener pressureListener;

    private class PressureListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            instance.lastPressure= event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        instance= Instance.getInstance(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        pressureSensor= sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        pressureListener= new PressureListener();

        if (pressureSensor != null){
            instance.pressureAvailable= true;
            sensorManager.registerListener(pressureListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            instance.pressureAvailable= false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        sensorManager.unregisterListener(pressureListener);
    }

    @Nullable @Override public IBinder onBind(Intent intent) { return null; }
}
