package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WeightWorkout} and makes a call to the
 * specified {@link ViewLoggedWorkoutsListFragment.OnLoggedWeightWorkoutsListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyViewLoggedWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<MyViewLoggedWorkoutsRecyclerViewAdapter.ViewHolder> {

    private final List<WeightWorkout> mValues;
    private final ViewLoggedWorkoutsListFragment.OnLoggedWeightWorkoutsListFragmentInteractionListener mListener;

    public MyViewLoggedWorkoutsRecyclerViewAdapter(List<WeightWorkout> items,
                           ViewLoggedWorkoutsListFragment
                           .OnLoggedWeightWorkoutsListFragmentInteractionListener listener) {
        if (items == null) {
            items = new ArrayList<WeightWorkout>();
        }
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_view_logged_workouts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mWorkoutName.setText(mValues.get(position).getWorkoutName());
        holder.mDateView.setText("Date Completed: " + mValues.get(position).getDate());
        holder.mWorkoutNumber.setText("Number: " + Integer.toString(mValues.get(position).getWorkoutNumber()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onViewLoggedWeightWorkoutsListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mWorkoutName;
        public final TextView mDateView;
        public final TextView mWorkoutNumber;

        public WeightWorkout mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mWorkoutName = (TextView) view.findViewById(R.id.workout_name);
            mDateView = (TextView) view.findViewById(R.id.workout_date);
            mWorkoutNumber = (TextView) view.findViewById(R.id.workout_number);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDateView.getText() + "'" + mWorkoutNumber.getText() + "'";
        }
    }
}
