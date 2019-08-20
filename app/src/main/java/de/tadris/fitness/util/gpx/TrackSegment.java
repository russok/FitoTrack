package de.tadris.fitness.util.gpx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class TrackSegment {

    @JacksonXmlElementWrapper(useWrapping = false)
    List<TrackPoint> trkpt;

    public TrackSegment(){

    }

    public TrackSegment(List<TrackPoint> trkpt) {
        this.trkpt = trkpt;
    }

    public List<TrackPoint> getTrkpt() {
        return trkpt;
    }

    public void setTrkpt(List<TrackPoint> trkpt) {
        this.trkpt = trkpt;
    }
}
