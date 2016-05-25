/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.DashboardActivity;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.ViewLoggedWorkoutsListFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;

/**
 * This is an adapter for the ViewLoggedWorkouts list. Populates the list with its contents.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class ViewLoggedWeightWorkoutsAdapter extends BaseAdapter {

    /** The workout number identifier. */
    private static final String NUMBER = "Number: ";

    /** The list of weight workouts for this user. */
    private final List<WeightWorkout> mValues;

    /** The listener for the list. */
    private final ViewLoggedWorkoutsListFragment.LoggedWeightWorkoutsInteractListener mListener;

    /** The Activity that holds this adapter. */
    private final Activity mContext;

    /**
     * Initializes a new ViewLoggedWeightWorkoutsAdapter.
     *
     * @param context the activity for this adapter
     * @param items the list of weight workouts
     * @param listener the listener for this adapter
     */
    public ViewLoggedWeightWorkoutsAdapter(Activity context, List<WeightWorkout> items,
                                           ViewLoggedWorkoutsListFragment.LoggedWeightWorkoutsInteractListener listener) {
        if (items == null) {
            items = new ArrayList<>();
        }
        mValues = items;
        mContext = context;
        mListener = listener;
    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return mValues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_view_logged_workouts, null);
        }
        final ViewHolder holder = new ViewHolder(convertView);
        holder.mItem = mValues.get(position);
        holder.mWorkoutName.setText(mValues.get(position).getWorkoutName());
        final String date = DashboardActivity.parseDate(mValues.get(position).getDate());
        final String number = NUMBER + mValues.get(position).getWorkoutNumber();
        holder.mDateView.setText(date);
        holder.mWorkoutNumber.setText(number);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.LoggedWeightWorkoutInteraction(holder.mItem);
                }
            }
        });
        return convertView;
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
