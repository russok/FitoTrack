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
