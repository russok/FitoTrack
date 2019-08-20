package de.tadris.fitness.util.gpx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TrackPoint {

    @JacksonXmlProperty(isAttribute = true)
    double lat;

    @JacksonXmlProperty(isAttribute = true)
    double lon;

    double ele;

    String time;

    String fix;

    TrackPointExtension extensions;

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
