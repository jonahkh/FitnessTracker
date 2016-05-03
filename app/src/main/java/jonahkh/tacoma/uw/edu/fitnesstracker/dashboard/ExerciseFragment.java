package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.MyExerciseRecyclerViewAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WeightWorkout;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnExerciseListFragmentInteractionListener}
 * interface.
 */
public class ExerciseFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String EXERCISE_SELECTED = "exercise_selected";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnExerciseListFragmentInteractionListener mListener;
    private List<Exercise> mExercises;
    private List<WeightWorkout> mWorkoutList;
    private RecyclerView mRecyclerView;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ExerciseFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            // TO DO:: connect to database and retrieve all exericses
//            ConnectivityManager connMgr = (ConnectivityManager)
//                    getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//            if (networkInfo != null && networkInfo.isConnected()) {
//                ViewExerciseTask task = new ViewExerciseTask();
//                task.execute(new String[]{WORKOUT_URL});
//            }
            // TODO: Delete this I think
            mExercises = new ArrayList<>();
            recyclerView.setAdapter(new MyExerciseRecyclerViewAdapter(mWorkoutList, mListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExerciseListFragmentInteractionListener) {
            mListener = (OnExerciseListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnExerciseListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnExerciseListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onExerciseListFragmentInteraction(WeightWorkout workout);
    }
}
