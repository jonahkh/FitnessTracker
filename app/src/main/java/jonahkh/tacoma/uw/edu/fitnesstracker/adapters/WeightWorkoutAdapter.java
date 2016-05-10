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
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.WeightWorkoutListFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for the list of weight workouts.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class WeightWorkoutAdapter extends RecyclerView.Adapter<WeightWorkoutAdapter.ViewHolder> {

    /** The list of exercises to be displayed. */
    private final List<Exercise> mValues;

    /** The listener for this adapter. */
    private final OnListFragmentInteractionListener mListener;

    /**
     * Initialize a new WeightWorkoutAdapter.
     *
     * @param items list of exercises to be populated
     * @param listener listener for this adapter
     */
    public WeightWorkoutAdapter(List<Exercise> items, OnListFragmentInteractionListener listener) {
        if (items == null) {
            items = new ArrayList<>();
        }
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weightworkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getExerciseName());
        holder.mContentView.setText("ex");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * Holds all of the view elements for each list item for this adapter.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /** The view holding all of the elements for this list item. */
        public final View mView;

        /** TextView holding the exercise name. */
        public final TextView mIdView;

        /** TextView holding the content. */
        public final TextView mContentView;

        /** The exercise for this list item. */
        public Exercise mItem;

        /**
         * Initialize a new ViewHolder.
         *
         * @param view the view that the elements will be added to
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
