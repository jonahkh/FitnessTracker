package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.PreDefinedWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.PreDefinedWorkoutFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PreDefinedWorkout} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPreDefinedWorkoutRecyclerViewAdapter extends RecyclerView.Adapter<MyPreDefinedWorkoutRecyclerViewAdapter.ViewHolder> {

    private final List<PreDefinedWorkout> mValues;
    private final OnListFragmentInteractionListener mListener;


    public MyPreDefinedWorkoutRecyclerViewAdapter(List<PreDefinedWorkout> items, OnListFragmentInteractionListener listener) {
        if (items == null) {
            items = new ArrayList<PreDefinedWorkout>();
        }
        mValues = items;
        mListener = listener;

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_predefinedworkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mContentView.setText(mValues.get(position).getName());
//        holder.mContentView.setText(mValues.get(position).getType());

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
//        public final ListView mIdView;
        public final TextView mContentView;
        public PreDefinedWorkout mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            mIdView = (ListView) view.findViewById(R.id.listView);
            mContentView = (TextView) view.findViewById(R.id.id);
//            mContentView = (ListView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
