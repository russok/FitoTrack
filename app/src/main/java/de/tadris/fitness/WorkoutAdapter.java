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
        holder.lengthText.setText(workouts[position].length + "km");
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
