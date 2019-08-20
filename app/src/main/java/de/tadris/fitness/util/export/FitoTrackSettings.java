package de.tadris.fitness.util.export;

public class FitoTrackSettings {

    String preferredUnitSystem;
    int weight;

    public FitoTrackSettings(){}

    public FitoTrackSettings(String preferredUnitSystem, int weight) {
        this.preferredUnitSystem = preferredUnitSystem;
        this.weight = weight;
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
}
