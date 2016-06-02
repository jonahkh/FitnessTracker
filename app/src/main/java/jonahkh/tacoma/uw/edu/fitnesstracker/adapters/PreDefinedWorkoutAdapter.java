/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.annotation.SuppressLint;
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
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.PreDefinedWorkoutFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;

/**
 * This class represents a layout adapter for the predefined workouts menu option.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class PreDefinedWorkoutAdapter extends BaseAdapter {

    /** The workouts to be added to the layout. */
    private final List<WeightWorkout> mValues;

    /** The listener for the list. */
    private final PreDefinedWorkoutFragment.PreDefinedWorkoutListener mListener;

    /** The Activity that holds this adapter. */
    private final Activity mContext;

    /**
     * Initialize a new PreDefinedWorkoutAdapter.
     *
     * @param context the activity for this adapter
     * @param items the list of predefined workouts
     * @param listener the listener for this adapter
     */
    public PreDefinedWorkoutAdapter(Activity context, List<WeightWorkout> items, PreDefinedWorkoutFragment.PreDefinedWorkoutListener listener) {
        if (items == null) {
            items = new ArrayList<>();
        }
        mContext = context;
        mValues = items;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_predefinedworkout, null);
        }
        final ViewHolder holder = new ViewHolder(convertView);
        holder.mContentView.setText(mValues.get(position).getWorkoutName());
        holder.mItem = mValues.get(position);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onPreDefinedWorkoutInteraction(holder.mItem);
                }
            }
        });
        return convertView;
    }

    /**
     * This class represents a ViewHolder for the PreDefinedWorkoutAdapter.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        /** The current View. */
        public final View mView;

        /** The text view for the workout name. */
        public final TextView mContentView;
        /** The current predefined workout. */
        public WeightWorkout mItem;

        /**
         * Initializes a new ViewHolder.
         *
         * @param view the current view
         */
        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.pre_defined_wo_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
