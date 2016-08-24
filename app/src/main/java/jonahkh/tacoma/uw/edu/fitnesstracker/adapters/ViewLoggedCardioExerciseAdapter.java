/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.CardioWorkout;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Cardio Workout list class.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class ViewLoggedCardioExerciseAdapter extends RecyclerView.Adapter<ViewLoggedCardioExerciseAdapter.ViewHolder> {

    /** The cadio workout header. */
    private static final String CARDIO_WORKOUT_HEADER = "Cardio Workout ";

    /** Miles for the distance. */
    private static final String MILES = " Miles";

    /** Minutes for formatting. */
    private static final String MINUTES = " Minutes";

    /** The list of cardio exercises for this user. */
    private final List<CardioWorkout> mValues;

    /** The Activity that holds this adapter. */
    private final Activity mContext;

//    /** The listener for the list. */
//    private final ViewLoggedCardioExerciseListFragment.LoggedCardioExerciseInteractListener mListener;

    /**
     * Constructor for this adapter.
     * @param context The context of the class using this adapter.
     * @param items The List of Cardio Workouts.
     */
    public ViewLoggedCardioExerciseAdapter(Activity context, List<CardioWorkout> items) {
        if (items == null) {
            items = new ArrayList<>();
        }
        mValues = items;
        mContext = context;
//        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cardio_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        final String cardioExerciseName = mValues.get(position).getActivityName();
        holder.mActivityNameView.setText(cardioExerciseName);

        final String date = mValues.get(position).getDate();
        holder.mDateView.setText(date);

        final String number = CARDIO_WORKOUT_HEADER + mValues.get(position).getWorkoutNumber();
        holder.mWorkoutNumberView.setText(number);

        final double distance =  mValues.get(position).getDistance();
        holder.mDistanceView.setText(distance + MILES);

        final int duration = mValues.get(position).getDuration();
        holder.mDurationView.setText(duration + MINUTES);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        /** The current exercise number view for this exercise. */
        private final TextView mWorkoutNumberView;

        /** The date view for this exercise. */
        private final TextView mDateView;

        /** The duration view for this exercise. */
        private final TextView mDurationView;

        /** The name view for this exercise. */
        private final TextView mActivityNameView;

        /** The distance view for this exercise. */
        private final TextView mDistanceView;

        /** The current CardioWorkout */
        public CardioWorkout mItem;

        /**
         * Initialize a new ViewHolder.
         *
         * @param view the view for this ViewHolder
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mWorkoutNumberView = (TextView) view.findViewById(R.id.cardio_exercise_number);
            mActivityNameView = (TextView) view.findViewById(R.id.cardio_exercise_name);
            mDistanceView = (TextView) view.findViewById(R.id.cardio_exercise_distance);
            mDateView = (TextView) view.findViewById(R.id.cardio_exercise_date);
            mDurationView = (TextView) view.findViewById(R.id.cardio_exercise_duration);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mActivityNameView.getText() + "'";
        }
    }
}
