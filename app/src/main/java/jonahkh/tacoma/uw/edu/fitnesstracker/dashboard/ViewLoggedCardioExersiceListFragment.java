package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.ViewLoggedCardioExerciseAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.CardioExercise;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class ViewLoggedCardioExersiceListFragment extends Fragment {

    public static final String CARDIO_EXERCISE_URL =
            "http://cssgate.insttech.washington.edu/~_450atm2/getCardioExercices.php?";

    /** so that we can access it in the thread to load the data. */
    private RecyclerView mRecyclerView;

    // TODO: Customize parameters
    private int mColumnCount = 1;

    /** The list of completed exercises for this user. */
    private List<CardioExercise> mWorkoutList;

    /** The adapter for this Fragment. */
    private ViewLoggedCardioExerciseAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewLoggedCardioExersiceListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cardio_exercise_list, container, false);
        String param = "email=" + getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

        }
        // check network
        if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.CardioExercises))) {
            DownloadCardioExercisesTask task = new DownloadCardioExercisesTask();
            task.execute(CARDIO_EXERCISE_URL + param);
        }
//        mAdapter = new ViewLoggedCardioExerciseAdapter(getActivity(), mWorkoutList);
        return view;
    }

    /**
     * Handles web service interaction for the ViewLoggedWorkoutsListFragment class.
     */
    private class DownloadCardioExercisesTask extends AsyncTask<String, Void, String> {

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

            mWorkoutList = new ArrayList<>();
//            Log.i("TEST", result.toString());
            result = CardioExercise.parseCardioExercisesJSON(result, mWorkoutList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mWorkoutList.isEmpty()) {
                mRecyclerView.setAdapter(new ViewLoggedCardioExerciseAdapter(getActivity(), mWorkoutList));
            } else {
                Toast.makeText(getActivity().getApplicationContext(),
                        R.string.no_cardioExe_to_display, Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
}
