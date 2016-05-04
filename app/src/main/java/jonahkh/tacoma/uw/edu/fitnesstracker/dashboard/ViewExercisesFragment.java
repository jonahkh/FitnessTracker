/*
 * Jonah Howard
 * Hector Diaz
 * TCSS 450 - Team 2
 */
package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.MyExerciseExpandableListAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.Exercise;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WeightWorkout;

/**
 * This Fragment displays all of the exercises that have been completed for the currently selected
 * workout in the View Logged Workouts menu option.
 *
 * A simple {@link Fragment} subclass.
 */
public class ViewExercisesFragment extends Fragment implements Serializable {

    /** The url for the web service to access the database that contains the exercises. */
    private static final String EXERCISE_URL
            = "http://cssgate.insttech.washington.edu/~_450atm2/workouts.php?cmd=exercise";

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

        SharedPreferences pref = getActivity()
                .getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE);
        if (mCurrentWorkout == null) {
            ((DashboardActivity) getActivity()).retrieveCurrentWorkout();
            mCurrentWorkout = ((DashboardActivity) getActivity()).getCurrentWorkout();
        }
        String param = "&email=" + pref.getString(getString(R.string.current_email),
                    "Email does not exist")
                    + "&workoutNumber=" + mCurrentWorkout.getWorkoutNumber();

        if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.exercises))) {
            DownloadWorkoutsTask task = new DownloadWorkoutsTask();
            task.execute(EXERCISE_URL + param);
        }
        mExerciseList = new ArrayList<>();
        mAdapter = new MyExerciseExpandableListAdapter(getActivity(), mExerciseList);
//        inflater.inflate(R.layout., container, false);
        return inflater.inflate(R.layout.fragment_view_exercises, container, false);
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
    private class DownloadWorkoutsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s;
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to download the list of exercises, Reason: "
                            + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
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
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mExerciseList.isEmpty()) {
                mAdapter = new MyExerciseExpandableListAdapter(getActivity(), mExerciseList);
                LayoutInflater inflater;
                ExpandableListView view = (ExpandableListView) getActivity()
                        .findViewById(R.id.specific_work_list);
                view.setAdapter(mAdapter);
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
        public int getDipsFromPixel(float pixels) {
            // Get the screen's density scale
            final float scale = getResources().getDisplayMetrics().density;
            // Convert the dps to pixels, based on density scale
            return (int) (pixels * scale + 250.5f);
        }
    }
}
