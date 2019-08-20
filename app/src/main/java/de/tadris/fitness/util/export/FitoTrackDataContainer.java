package de.tadris.fitness.util.export;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;

@JacksonXmlRootElement(localName = "fito-track")
public class FitoTrackDataContainer {

    int version;
    List<Workout> workouts;
    List<WorkoutSample> samples;
    FitoTrackSettings settings;

    public FitoTrackDataContainer(){}

    public FitoTrackDataContainer(int version, List<Workout> workouts, List<WorkoutSample> samples, FitoTrackSettings settings) {
        this.version = version;
        this.workouts = workouts;
        this.samples = samples;
        this.settings = settings;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }

    public List<WorkoutSample> getSamples() {
        return samples;
    }

    public void setSamples(List<WorkoutSample> samples) {
        this.samples = samples;
    }

    public FitoTrackSettings getSettings() {
        return settings;
    }

    public void setSettings(FitoTrackSettings settings) {
        this.settings = settings;
    }
}
