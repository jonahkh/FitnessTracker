package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewExercisesFragment extends Fragment implements Serializable {
    public static final String WORKOUT_SELECTED = "workout_selected";
    public static final String EXERCISE_URL = "http://cssgate.insttech.washington.edu/~_450atm2/workouts.php?cmd=exercise";

    private Map<Exercise, List<WorkoutSet>> mExercises;
    private List<WeightWorkout> mWorkoutList;
    private List<Exercise> mExerciseList;
    private MyExerciseExpandableListAdapter mAdapter;
    private String mCurrentExercise;

    public ViewExercisesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("EXERCISEFRAG", "EXERCISEFRAG");
        View view = inflater.inflate(R.layout.fragment_view_exercises, container, false);
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        SharedPreferences pref = getActivity().getSharedPreferences(getString(R.string.WORKOUT_INFO), Context.MODE_PRIVATE);
        String param = "&email=" + pref.getString(getString(R.string.current_email), "Email does not exist")
                + "&exercise=" + pref.getString(getString(R.string.current_workout), "Workout does not exist");
        if (networkInfo != null && networkInfo.isConnected()) {
            DownloadWorkoutsTask task = new DownloadWorkoutsTask();
            task.execute(new String[]{EXERCISE_URL + param});
        }
        populateExercises();
        mAdapter = new MyExerciseExpandableListAdapter(getActivity(), mExerciseList, mExercises);
        return inflater.inflate(R.layout.fragment_view_exercises, container, false);
    }

    private void populateExercises() {
        mExerciseList = new ArrayList<Exercise>();
    }

    public void setCurrentExercise(String exerciseName) {
        mCurrentExercise = exerciseName;
    }



    private class DownloadWorkoutsTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {super.onPreExecute();}

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
                    String s = "";
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

            mWorkoutList = new ArrayList<WeightWorkout>();
            result = WeightWorkout.parseWeightWorkoutJSON(result, mWorkoutList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mWorkoutList.isEmpty()) {
//                mRecyclerView.setAdapter(new MyWeightWorkoutRecyclerViewAdapter(mWorkoutList, mListener));
            }
        }

    }

}