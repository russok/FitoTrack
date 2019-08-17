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

package de.tadris.fitness;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.tadris.fitness.data.Workout;
import de.tadris.fitness.util.UnitUtils;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>{


    public static class WorkoutViewHolder extends RecyclerView.ViewHolder{

        View root;
        TextView lengthText, timeText;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            this.root= itemView;
            lengthText= itemView.findViewById(R.id.workoutLength);
            timeText=   itemView.findViewById(R.id.workoutTime);
        }
    }

    Workout[] workouts;
    WorkoutAdapterListener listener;

    public WorkoutAdapter(Workout[] workouts, WorkoutAdapterListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @Override
    public WorkoutAdapter.WorkoutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_workout, parent, false);
        return new WorkoutViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WorkoutViewHolder holder, final int position) {
        holder.lengthText.setText(UnitUtils.getDistance(workouts[position].length));
        holder.timeText.setText(UnitUtils.getHourMinuteTime(workouts[position].getTime()));
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(workouts[position]);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return workouts.length;
    }

    interface WorkoutAdapterListener{
        void onItemClick(Workout workout);
    }


}
