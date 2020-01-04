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

package de.tadris.fitness.util.gpx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

class TrackPoint {

    @JacksonXmlProperty(isAttribute = true)
    private
    double lat;

    @JacksonXmlProperty(isAttribute = true)
    private
    double lon;

    private double ele;

    private String time;

    private String fix;

    private TrackPointExtension extensions;

    public TrackPoint(){}

    public TrackPoint(double lat, double lon, double ele, String time, String fix, TrackPointExtension extensions) {
        this.lat = lat;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
        this.fix = fix;
        this.extensions = extensions;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getEle() {
        return ele;
    }

    public void setEle(double ele) {
        this.ele = ele;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFix() {
        return fix;
    }

    public void setFix(String fix) {
        this.fix = fix;
    }

    public TrackPointExtension getExtensions() {
        return extensions;
    }

    public void setExtensions(TrackPointExtension extensions) {
        this.extensions = extensions;
    }
}
