package de.tadris.fitness.util.gpx;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "gpx")
public class Gpx {

    @JacksonXmlProperty(isAttribute = true)
    String version;

    @JacksonXmlProperty(isAttribute = true)
    String creator;

    Metadata metadata;

    String name;
    String desc;

    @JacksonXmlElementWrapper(useWrapping = false)
    List<Track> trk;

    public Gpx(){}

    public Gpx(String version, String creator, Metadata metadata, String name, String desc, List<Track> trk) {
        this.version = version;
        this.creator = creator;
        this.metadata = metadata;
        this.name = name;
        this.desc = desc;
        this.trk = trk;
    }

    public String getVersion() {
        return version;
    }

    public String getCreator() {
        return creator;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public List<Track> getTrk() {
        return trk;
    }
}
