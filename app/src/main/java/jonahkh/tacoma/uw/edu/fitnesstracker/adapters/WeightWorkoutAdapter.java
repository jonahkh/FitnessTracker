package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.WeightWorkoutListFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;

/**
 * Created by jonah on 5/13/2016.
 */
public class WeightWorkoutAdapter extends BaseAdapter {

    /** The list of exercises to be displayed. */
    private final List<Exercise> mValues;

    /** The listener for this adapter. */
    private final WeightWorkoutListFragment.OnListFragmentInteractionListener mListener;

    /** The activity that holds this adapter. */
    private final Activity mContext;

    /**
     * Initialize a new WeightWorkoutAdapter.
     *
     * @param context the Activity for this adapter
     * @param items list of exercises to be populated
     * @param listener listener for this adapter
     */
    public WeightWorkoutAdapter(Activity context, List<Exercise> items,
                                      WeightWorkoutListFragment.OnListFragmentInteractionListener listener) {
        if (items == null) {
            items = new ArrayList<>();
        }
        mValues = items;
        mListener = listener;
        mContext = context;
    }



    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        Log.e("ADAPTER", ((Exercise) mValues.get(position)).getExerciseName());
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
            convertView = inflater.inflate(R.layout.fragment_weightworkout, null);
        }
        final ViewHolder holder = new ViewHolder(convertView);
        holder.mItem = mValues.get(position);
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getExerciseName());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    Log.e("ADAPTER", ((Exercise) holder.mItem).getExerciseName());
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        return convertView;
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
