package de.tadris.fitness.util.gpx;

public class Metadata {

    String name;
    String desc;
    String time;

    public Metadata() {
    }

    public Metadata(String name, String desc, String time) {
        this.name = name;
        this.desc = desc;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
