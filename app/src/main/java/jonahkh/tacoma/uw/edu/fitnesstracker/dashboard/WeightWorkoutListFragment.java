/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
        ((DashboardActivity) getActivity()).lockDrawer(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        View view = inflater.inflate(R.layout.fragment_custom_weight_list, container, false);
        if (mCurrentWorkout != null) {
            // Retrieve current workout
            mCurrentWorkout = ((DashboardActivity) getActivity()).getCurrentWorkout();
            mBundle = getArguments();
            String param;
            // Determine if user is using a workout template or reusing a previously completed
            // workout
            if (mBundle != null && mBundle.getBoolean("is_custom")) {
                param = "&num=" + mCurrentWorkout.getWorkoutNumber();
            } else {
                param = "&name=" + mCurrentWorkout.getWorkoutName();
            }
            if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.workouts))) {
                DownloadWeightWorkoutsTask task = new DownloadWeightWorkoutsTask();
                task.execute(WORKOUT_URL + param);
            } else {
                Toast.makeText(view.getContext(),
                        "No network connection available. Cannot display workouts",
                        Toast.LENGTH_SHORT).show();
            }
        } else {    // A custom workout is being performed
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
        ListView list = (ListView)  view.findViewById(R.id.custom_workout_list);
        list.setAdapter(mAdapter);
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
        @SuppressLint("InflateParams") final View v = inflater.inflate(R.layout.add_exercise_fragment, null);
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
                if (text.length() < 1) {
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
    private void addExercise(final String exercise) {
        mExerciseList.add(new Exercise(exercise.trim()));
        mAdapter.notifyDataSetChanged();
//        view.setAdapter(mAdapter);
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
        //TODO check if result = network error. If so, then try with new url to pull from recorded workouts with email workout number
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
                mAdapter = new WeightWorkoutAdapter(getActivity(), mExerciseList, mListener);
                ListView view = (ListView) getActivity().findViewById(R.id.custom_workout_list);
                view.setAdapter(mAdapter);
            }
        }
    }

    @Override
    public void onDestroy() {
        ((DashboardActivity) getActivity()).lockDrawer(DrawerLayout.LOCK_MODE_UNLOCKED);
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
                if (mExerciseList.isEmpty()) {
                    Toast.makeText(getActivity(), "Workout Saved", Toast.LENGTH_SHORT).show();
                }
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.popBackStack();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // Do nothing
            }
        });
        Dialog alert = builder.create();
            alert.show();
        ((DashboardActivity) getActivity()).lockDrawer(DrawerLayout.LOCK_MODE_UNLOCKED);
    }
}
