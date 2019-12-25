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

package de.tadris.fitness.export;

public class FitoTrackSettings {

    String preferredUnitSystem;
    int weight;
    String mapStyle;

    public FitoTrackSettings(){}

    public FitoTrackSettings(String preferredUnitSystem, int weight, String mapStyle) {
        this.preferredUnitSystem = preferredUnitSystem;
        this.weight = weight;
        this.mapStyle = mapStyle;
    }

    public String getPreferredUnitSystem() {
        return preferredUnitSystem;
    }

    public void setPreferredUnitSystem(String preferredUnitSystem) {
        this.preferredUnitSystem = preferredUnitSystem;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getMapStyle() {
        return mapStyle;
    }

    public void setMapStyle(String mapStyle) {
        this.mapStyle = mapStyle;
    }
}
