package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.ViewLoggedCardioExersiceListFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.CardioExercise;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CardioExercise} and makes a call to the
 * specified {@link ViewLoggedCardioExerciseAdapter}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ViewLoggedCardioExerciseAdapter extends RecyclerView.Adapter<ViewLoggedCardioExerciseAdapter.ViewHolder> {

    /** The workout number identifier. */
    public static final String NUMBER = "Number: ";

    /** Miles for the distance. */
    public static final String MILES = " Miles";

    /** Minutes for formatting. */
    public static final String MINUTES = " Minutes";

    /** The list of cardio exercises for this user. */
    private final List<CardioExercise> mValues;

    /** The Activity that holds this adapter. */
    private final Activity mContext;

//    /** The listener for the list. */
//    private final ViewLoggedCardioExersiceListFragment.LoggedCardioExerciseInteractListener mListener;

    public ViewLoggedCardioExerciseAdapter(Activity context, List<CardioExercise> items) {
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

        final String number = NUMBER + mValues.get(position).getWorkoutNumber();
        holder.mWorkoutNumberView.setText(number);

        final double distance =  mValues.get(position).getDistance();
        holder.mDistanceView.setText(distance + MILES);

        final int duration = mValues.get(position).getDuration();
        holder.mDurationView.setText(duration + MINUTES);
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            if (null != mListener) {
//                // Notify the active callbacks interface (the activity, if the
//                // fragment is attached to one) that an item has been selected.
//                mListener.LoggedCardioExerciseInteraction(holder.mItem);
//            }
//            }
//        });
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

        /** The current CardioExercise */
        public CardioExercise mItem;

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
