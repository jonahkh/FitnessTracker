/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.LoggedExerciseAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.RestClient;

/**
 * This Fragment displays all of the exercises that have been completed for the currently selected
 * workout in the View Logged Workouts menu option.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class ViewExercisesFragment extends Fragment implements Serializable {
    private static final String BASE_ENDPOINT = "localhost:5000/v1/fitnesstracker/";

    /** The url for the web service to access the database that contains the exercises. */
//    private static final String EXERCISE_URL
//            = "localhost:5000/v1/fitnesstracker/workouts.php?cmd=exercise";

    /** The Exercises being displayed. */
    private List<Exercise> mExerciseList;

    /** The adapter for this Fragment. */
    private BaseExpandableListAdapter mAdapter;

    /** The current workout. */
    private WeightWorkout mCurrentWorkout;

    /**Initialize a new ViewExerciseFragment. */
    public ViewExercisesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_exercises, container, false);
        SharedPreferences pref = getActivity()
                .getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE);
        if (mCurrentWorkout == null) {
            mCurrentWorkout = ((DashboardActivity) getActivity()).getCurrentWorkout();
        }
        String email = pref.getString(getString(R.string.current_email),
                "Email does not exist");
        String param = "&email=" + email
                    + "&workoutNumber=" + mCurrentWorkout.getWorkoutNumber();

        if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.exercises))) {
            DownloadExercisesTask task = new DownloadExercisesTask();
            task.execute(BASE_ENDPOINT + "downloadexercise/" + email + "/?workoutNumber=" + mCurrentWorkout.getWorkoutNumber());
        }
        mExerciseList = new ArrayList<>();
        mAdapter = new LoggedExerciseAdapter(getActivity(), mExerciseList);
//        inflater.inflate(R.layout., container, false);
//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("https://developers.facebook.com"))
//                .build();
//        ShareContent content = new ShareContent.Builder().build();
        Bundle bundle = new Bundle();
        bundle.putString("message", "This is a test");
//        ShareDialog dialog = new ShareDialog(getActivity());
        return view;
    }

    /**
     * Set the current workout to the passed workout.
     *
     * @param workout the new workout for this Fragment
     */
    public void setWorkout(WeightWorkout workout) {
        mCurrentWorkout = workout;
    }

    /**
     * This class handles all interactions with the web service pertaining to this Fragment.
     */
    private class DownloadExercisesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return RestClient.runRequest("GET", null, urls);
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
            if (mCurrentWorkout == null) {
               mCurrentWorkout = ((DashboardActivity) getActivity()).getCurrentWorkout();
            }
            result = WeightWorkout.parseExercisesJSON(result, mExerciseList);
            // Something wrong with the JSON returned.
            if (null != result) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of exercises.
            if (!mExerciseList.isEmpty()) {
                mAdapter = new LoggedExerciseAdapter(getActivity(), mExerciseList);
                ExpandableListView view = (ExpandableListView) getActivity()
                        .findViewById(R.id.specific_work_list);
                view.setAdapter(mAdapter);
                // Set the indicator bounds for the expandable list item (displayed as an arrow)
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int width = metrics.widthPixels;
                view.setIndicatorBounds(width - getDipsFromPixel(10), width - getDipsFromPixel(-50));
            }
        }

        /**
         * Converts the passed value to pixels (integer).
         *
         * @param pixels the value being converted
         * @return the converted value
         */
        private int getDipsFromPixel(float pixels) {
            // Get the screen's density scale
            final float scale = getResources().getDisplayMetrics().density;
            // Convert the dps to pixels, based on density scale
            return (int) (pixels * scale + 250.5f);
        }
    }
}
