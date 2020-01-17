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

package de.tadris.fitness.export;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

import de.tadris.fitness.data.Workout;
import de.tadris.fitness.data.WorkoutSample;

@JacksonXmlRootElement(localName = "fito-track")
@JsonIgnoreProperties(ignoreUnknown = true)
class FitoTrackDataContainer {

    private int version;
    private List<Workout> workouts;
    private List<WorkoutSample> samples;

    public FitoTrackDataContainer(){}

    public FitoTrackDataContainer(int version, List<Workout> workouts, List<WorkoutSample> samples) {
        this.version = version;
        this.workouts = workouts;
        this.samples = samples;
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


}
