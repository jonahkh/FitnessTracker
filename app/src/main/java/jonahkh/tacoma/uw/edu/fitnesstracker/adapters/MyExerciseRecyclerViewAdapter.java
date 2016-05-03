package jonahkh.tacoma.uw.edu.fitnesstracker.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.dashboard.ExerciseFragment;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WeightWorkout;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Exercise} and makes a call to the
 * specified {@link ExerciseFragment.OnExerciseListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyExerciseRecyclerViewAdapter extends RecyclerView.Adapter<MyExerciseRecyclerViewAdapter.ViewHolder> {

    private final List<WeightWorkout> mValues;
    private final ExerciseFragment.OnExerciseListFragmentInteractionListener mListener;

    public MyExerciseRecyclerViewAdapter(List<WeightWorkout> items, ExerciseFragment.OnExerciseListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getWorkoutName());
        holder.mContentView.setText("");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onExerciseListFragmentInteraction(holder.mItem);
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
        public final TextView mIdView;
        public final TextView mContentView;
        public WeightWorkout mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.workout_name);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
