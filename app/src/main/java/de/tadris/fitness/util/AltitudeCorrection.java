/*
 * Copyright (c) 2020 Jannis Scheibe <jannis@tadris.de>
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

package de.tadris.fitness.util;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import de.tadris.fitness.R;

/**
 * This class has the task to correct the altitude.
 * <p>
 * In Germany or UK for example the GPS height differs
 * roughly 50m from the real elevation over sea level.
 * <p>
 * The altitude given by GPS is the altitude over the WGS84 reference ellipsoid
 * but we want the height over the sea level. That's why we have to correct the height.
 * Luckily I found a file containing the corrections for all places around the world.
 * <p>
 * The geoids.csv is from https://github.com/vectorstofinal/geoid_heights licensed under MIT
 */
public class AltitudeCorrection {

    private Context context;
    private int latitude, longitude;
    private double offset; // Basically how much higher the sea-level than the ellipsoid is

    public AltitudeCorrection(Context context, int latitude, int longitude) throws IOException {
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        findOffset();
    }

    private void findOffset() throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.geoids);
        List<String> list = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
        for (String line : list) {
            String[] data = line.split(",");
            int lat = Integer.parseInt(data[0]);
            int lon = Integer.parseInt(data[1]);
            double offset = Double.parseDouble(data[2]);
            if (lat == this.latitude && lon == this.longitude) {
                this.offset = offset;
                return;
            }
        }
    }

    public double getHeightOverSeaLevel(double heightOverEllipsoid) {
        return heightOverEllipsoid - offset;
    }
}
