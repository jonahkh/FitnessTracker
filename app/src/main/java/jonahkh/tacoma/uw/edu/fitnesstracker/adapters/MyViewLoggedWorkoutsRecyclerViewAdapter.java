/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.ViewLoggedWorkoutsListFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WeightWorkout;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WeightWorkout} and makes a call to the
 * specified {@link ViewLoggedWorkoutsListFragment.OnLoggedWeightWorkoutsListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyViewLoggedWorkoutsRecyclerViewAdapter extends RecyclerView.Adapter<MyViewLoggedWorkoutsRecyclerViewAdapter.ViewHolder> {

    /** The date completed identifier. */
    private static final String DATE = "Date Completed: ";

    /** The workout number identifier. */
    private static final String NUMBER = "Number: ";

    /** The list of weight workouts for this user. */
    private final List<WeightWorkout> mValues;

    /** The listener for this adapter. */
    private final ViewLoggedWorkoutsListFragment
            .OnLoggedWeightWorkoutsListFragmentInteractionListener mListener;

    /**
     * Initialize a new MyViewLoggedWorkoutsRecyclerViewAdapter.
     *
     * @param items the list of weight workouts for this user
     * @param listener the listener for this adapter
     */
    public MyViewLoggedWorkoutsRecyclerViewAdapter(List<WeightWorkout> items,
                           ViewLoggedWorkoutsListFragment
                           .OnLoggedWeightWorkoutsListFragmentInteractionListener listener) {
        if (items == null) {
            items = new ArrayList<>();
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
        final String date = DATE + mValues.get(position).getDate();
        final String number = NUMBER + mValues.get(position).getWorkoutNumber();
        holder.mDateView.setText(date);
        holder.mWorkoutNumber.setText(number);

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

    /**
     * This class handles the display for each list item of the completed workouts list.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** The view for this ViewHolder. */
        public final View mView;

        /** Displays the workout name. */
        public final TextView mWorkoutName;

        /** Displays the date this workout was completed. */
        public final TextView mDateView;

        /** Displays the workout number for this workout. */
        public final TextView mWorkoutNumber;

        /** The current Weight Workout. */
        public WeightWorkout mItem;

        /**
         * Initialize a new ViewHolder.
         *
         * @param view the view for this ViewHolder
         */
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
