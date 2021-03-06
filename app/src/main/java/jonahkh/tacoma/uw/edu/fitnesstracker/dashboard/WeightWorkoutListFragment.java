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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.WeightWorkoutAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.domain.DownloadWeightWorkoutsRequest;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.JsonUtilities;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.RestClient;

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
    private static final String BASE_ENDPOINT = "localhost:5000/v1/fitnesstracker/";

    /** The url to retrieve information from the database for this app. */
    private static final String WORKOUT_URL
            = "localhost:5000/v1/fitnesstracker/workouts.php?cmd=weightworkouts";

    /** The listener for this list. */
    private OnListFragmentInteractionListener mListener;

    /** The current list of exercises. */
    private List<Exercise> mExerciseList;

    /** The current list of workouts. */
    private WeightWorkout mCurrentWorkout;

    /** The add exercise dialog. */
    private Dialog mDialog;

    /** The adapter for this list fragment. */
    private BaseAdapter mAdapter;

    /** Bundle for this Fragment. */
    private Bundle mBundle;

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
        View view = inflater.inflate(R.layout.fragment_custom_weight_list, container, false);
        mBundle = getArguments();
        if (mCurrentWorkout != null) {
            // Retrieve current workout
            mCurrentWorkout = ((DashboardActivity) getActivity()).getCurrentWorkout();
            mBundle = getArguments();
            String param;
            // Determine if user is using a workout template or reusing a previously completed
            // workout
            if (mBundle != null && mBundle.getBoolean("is_custom")) {
                param = "&workoutNumber=" + mCurrentWorkout.getWorkoutNumber();
            } else {
                param = "&workoutName=" + mCurrentWorkout.getWorkoutName();
            }
            if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.workouts))) {
                String url = BASE_ENDPOINT + "downloadweightworkout/?workoutName=" + param;
//                        + mCurrentWorkout.getWorkoutName()
//                        + "&workoutNumber=" + mCurrentWorkout.getWorkoutNumber();
                DownloadWeightWorkoutsTask task = new DownloadWeightWorkoutsTask(new DownloadWeightWorkoutsRequest(mCurrentWorkout.getWorkoutName(), mCurrentWorkout.getWorkoutNumber()));
                task.execute(url);
            } else {
                Toast.makeText(view.getContext(),
                        "No network connection available. Cannot display workouts",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (mExerciseList == null){    // A custom workout is being performed
            mExerciseList = new ArrayList<>();
        }

        setDialog();
        final Button button = (Button) view.findViewById(R.id.add_exercise_button);
        final Button cancel = (Button) view.findViewById(R.id.finish_workout);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExit();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
            }
        });
        mAdapter = new WeightWorkoutAdapter(getActivity(), mExerciseList, mListener);
        // Handle when the user presses the back button
        ListView list = (ListView) view.findViewById(R.id.custom_workout_list);
        list.setAdapter(mAdapter);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        return view;
    }

    /**
     * Set the current workout to the passed value.
     *
     * @param workout the workout for this list of exercises
     */
    public void setWorkout(final WeightWorkout workout) {
        mCurrentWorkout = workout;
    }
    /**
     * Creates the dialog that appears when the user is in a custom workout and presses
     * the "Add Exercise" button at the bottom of the screen. When the user enters an exercise name
     * and presses the add button on the dialog, a new exercise is displayed in the list of
     * exercises.
     */
    private void setDialog() {
        final DashboardActivity dashboard = (DashboardActivity) getActivity();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = dashboard.getLayoutInflater();
        final View v = inflater.inflate(R.layout.add_exercise_fragment, null);
        final TextView mExercise = (EditText) v.findViewById(R.id.enter_exercise_name);
        final Button addExercise = (Button) v.findViewById(R.id.add_exercise);
        builder.setView(v);
        builder.setTitle("Enter Exercise Name");
        final Dialog dialog = builder.create();
        Button cancel = (Button) v.findViewById(R.id.cancel_exercise);

        addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = mExercise.getText().toString();
                if (null == text || text.length() < 1) {
                    mExercise.setError("Required Field!");
                } else {
                    addExercise(mExercise.getText().toString());
                    mExercise.setText("");
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExercise.setText("");
                Toast.makeText(getActivity().getApplicationContext(), "Exercise not added!",
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mExercise.setText("");
            }
        });
        mDialog = dialog;
    }

    /**
     * Add a new exercise to the list view with the given name.
     *
     * @param exercise the name of the exercise
     */
    public void addExercise(final String exercise) {
        mExerciseList.add(new Exercise(exercise.trim()));
        mAdapter.notifyDataSetChanged();
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
        private DownloadWeightWorkoutsRequest request;

        DownloadWeightWorkoutsTask(DownloadWeightWorkoutsRequest request) {
            this.request = request;
        }

        @Override
        protected String doInBackground(String... urls) {

            return RestClient.runRequest("POST", null, urls);
        }

        @Override
        protected void onPostExecute(String result) {

            mExerciseList = JsonUtilities.fromJson(result, new TypeToken<List<Exercise>>(){});
            boolean checkType = false;
            if (mBundle != null) {
                checkType = mBundle.getBoolean("is_custom");
            }
            result = WeightWorkout.parseWeightWorkoutListExerciseJSON(result, mExerciseList,
                    checkType);
            // Something wrong with the JSON returned.
            if (result != null) {

                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            mAdapter = new WeightWorkoutAdapter(getActivity(), mExerciseList, mListener);
            // Everything is good, show the list of courses.
            if (!mExerciseList.isEmpty()) {
                Log.e("WWLF", "here");
                mAdapter = new WeightWorkoutAdapter(getActivity(), mExerciseList, mListener);
                ListView view = (ListView) getActivity().findViewById(R.id.custom_workout_list);
                view.setAdapter(mAdapter);
                view.setLongClickable(true);
                view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.e("WWLF", "long click");
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
//        ((DashboardActivity) getActivity()).lockDrawer(DrawerLayout.LOCK_MODE_UNLOCKED);
        super.onDestroy();
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
