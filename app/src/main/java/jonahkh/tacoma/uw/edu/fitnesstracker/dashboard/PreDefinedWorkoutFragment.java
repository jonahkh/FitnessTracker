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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.PreDefinedWorkoutAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.model.WeightWorkout;
import jonahkh.tacoma.uw.edu.fitnesstracker.services.RestClient;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of PreDefined Workouts.
 *
 * @author Jonah Howard
 * @author Hector Diaz
 */
public class PreDefinedWorkoutFragment extends Fragment {
    private static final String BASE_ENDPOINT = "localhost:5000/v1/fitnesstracker/";
    /** The url to fetch data from the mysql server. */

    private static final String WORKOUT_URL
            = "localhost:5000/v1/fitnesstracker/getpredefinedworkouts";

    /** The listener for this Fragment. */
    private PreDefinedWorkoutListener mListener;

    /** The list of predefined workouts. */
    private List<WeightWorkout> mWorkoutList;

    /** The adapter for this Fragment. */
    private BaseAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PreDefinedWorkoutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_predefined_workouts_list, container, false);
        SharedPreferences sharedPrefs =
                getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS)
                        , Context.MODE_PRIVATE);
        String email = sharedPrefs.getString(getActivity().getString(R.string.current_email),
                "No existing email");
        if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.workouts))) {
            DownloadPreDefinedWorkoutsTask task = new DownloadPreDefinedWorkoutsTask();
            task.execute(WORKOUT_URL);
        }

        mAdapter = new PreDefinedWorkoutAdapter(getActivity(), mWorkoutList, mListener);
        return view;
    }

    @Override
    public void onResume() {
        ((DashboardActivity) getActivity()).setNavigationItem(R.id.nav_predefined_workouts);
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PreDefinedWorkoutListener) {
            mListener = (PreDefinedWorkoutListener) context;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface PreDefinedWorkoutListener {
        void onPreDefinedWorkoutInteraction(WeightWorkout workout);
    }

    /**
     * This class handles the interactions with the web service for this Fragment.
     */
    private class DownloadPreDefinedWorkoutsTask extends AsyncTask<String, Void, String> {

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
            mWorkoutList = new ArrayList<>();
            result = WeightWorkout.parsePreDefinedWeightWorkoutJSON(result, mWorkoutList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }
            mAdapter = new PreDefinedWorkoutAdapter(getActivity(), mWorkoutList, mListener);
            // Everything is good, show the list of workouts.
            if (!mWorkoutList.isEmpty()) {
                mAdapter = new PreDefinedWorkoutAdapter(getActivity(), mWorkoutList, mListener);
                ListView view = (ListView) getActivity().findViewById(R.id.predefined_wo_list);
                view.setAdapter(mAdapter);
            }
        }
    }
}
