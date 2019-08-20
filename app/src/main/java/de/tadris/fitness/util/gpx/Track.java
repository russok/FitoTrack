package de.tadris.fitness.util.gpx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.List;

public class Track {

    String name;
    String cmt;
    String desc;
    String src;
    int number;
    String type;

    @JacksonXmlElementWrapper(useWrapping = false)
    List<TrackSegment> trkseg;

    public Track(){}

    public Track(String name, String cmt, String desc, String src, int number, String type, List<TrackSegment> trkseg) {
        this.name = name;
        this.cmt = cmt;
        this.desc = desc;
        this.src = src;
        this.number = number;
        this.type = type;
        this.trkseg = trkseg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TrackSegment> getTrkseg() {
        return trkseg;
    }

    public void setTrkseg(List<TrackSegment> trkseg) {
        this.trkseg = trkseg;
    }
}
