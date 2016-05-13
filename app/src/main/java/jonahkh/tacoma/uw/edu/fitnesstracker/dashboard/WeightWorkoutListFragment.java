/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.WeightWorkoutAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;

/**
 * A fragment representing a list of WeightWorkouts. All of the predefined workouts from the
 * database are displayed as well as all of the custom made workouts that are stored locally.
 * Selecting one of the workouts allows a user to start a workout where they can record the
 * exercises/sets they complete.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class WeightWorkoutListFragment extends Fragment {

    /** The url to retrieve information from the database for this app. */
    private static final String WORKOUT_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/workouts.php?cmd=weightworkouts";

    /** The listener for this list. */
    private OnListFragmentInteractionListener mListener;

    /** The adapter for this list. */
    private RecyclerView mRecyclerView;

    /** The current list of exercises. */
    private List<Exercise> mExerciseList;

    /** The current list of workouts. */
    private WeightWorkout mCurrentWorkout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeightWorkoutListFragment() {
    }


    /**
     * Set the workout name to the passed value.
     *
     * @param name the new name for this workout
     */
    public void setName(String name) {
        mCurrentWorkout = new WeightWorkout(name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weightworkout_list, container, false);

        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        // Retrieve current workout
        if (mCurrentWorkout == null) {
            ((DashboardActivity) getActivity()).retrieveCurrentWorkout();
            mCurrentWorkout = ((DashboardActivity) getActivity()).getCurrentWorkout();
        }
        String param = "&name=" + mCurrentWorkout.getWorkoutName();
        if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.workouts))) {
            DownloadWeightWorkoutsTask task = new DownloadWeightWorkoutsTask();
            task.execute(WORKOUT_URL + param);
        } else {
            Toast.makeText(view.getContext(),
                    "No network connection available. Cannot display workouts",
                    Toast.LENGTH_SHORT) .show();
        }
        mRecyclerView.setAdapter(new WeightWorkoutAdapter(mExerciseList, mListener));
        // Handle when the user presses the back button
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    onExit();
                    return true;
                }
                return false;
            }
        });
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
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
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Exercise exercise);
    }

    /**
     * This class handles all of the web service interaction for the WeightWorkoutL
     */
    private class DownloadWeightWorkoutsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return DashboardActivity.doInBackgroundHelper(urls);
        }

        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            if (result.startsWith("Unable to")) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            mExerciseList = new ArrayList<>();
            result = WeightWorkout.parseWeightWorkoutListExerciseJSON(result, mExerciseList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mExerciseList.isEmpty()) {
                mRecyclerView.setAdapter(new WeightWorkoutAdapter(mExerciseList, mListener));


            }
        }
    }

    /**
     * Verify with the user that they want to exit the current workout.
     */
    private void onExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Exit this workout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), "Workout Saved", Toast.LENGTH_SHORT).show();
                ((DashboardActivity) getActivity()).setFirstAddedExercise(true);
                getActivity().onBackPressed();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });
        Dialog alert = builder.create();
            alert.show();
    }
}
