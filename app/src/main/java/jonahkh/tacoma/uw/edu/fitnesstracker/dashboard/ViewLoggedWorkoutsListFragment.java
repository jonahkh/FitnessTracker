package jonahkh.tacoma.uw.edu.fitnesstracker.dashboard;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import jonahkh.tacoma.uw.edu.fitnesstracker.R;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.MyViewLoggedWorkoutsRecyclerViewAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.adapters.ViewLoggedWorkoutsAdapter;
import jonahkh.tacoma.uw.edu.fitnesstracker.types.WeightWorkout;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Logged Workouts. Logged workouts display the workout name, the
 * date completed, and the workout number (unique for each workout for each user). Selecting one of
 * the logged workouts allows you to view all of the exercises completed for that workout and the
 * sets pertinent to that exercise.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnLoggedWeightWorkoutsListFragmentInteractionListener}
 * interface.
 */
public class ViewLoggedWorkoutsListFragment extends Fragment {

    /** The url to access the database for this application. */
    private static final String WORKOUT_URL
            = "http://cssgate.insttech.washington.edu/" +
            "~_450atm2/workouts.php?cmd=loggedweightworkouts";

    /** The listener for this Fragment. */
    private OnLoggedWeightWorkoutsListFragmentInteractionListener mListener;

    /** The list of completed workouts for this user. */
    private List<WeightWorkout> mWorkoutList;

    /** The current adapter for this list. */
    private RecyclerView mRecyclerView;

    /** The adapter for this Fragment. */
    private BaseAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViewLoggedWorkoutsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_logged_workouts_list, container, false);
        String param = "&email=" + getActivity().getSharedPreferences(getString(R.string.LOGIN_PREFS),
                Context.MODE_PRIVATE).getString(getString(R.string.current_email),
                "Email does not exist");
        if (((DashboardActivity) getActivity()).isNetworkConnected(getString(R.string.workouts))) {
            DownloadWorkoutsTask task = new DownloadWorkoutsTask();
            task.execute(WORKOUT_URL + param);
        }
        mAdapter = new ViewLoggedWorkoutsAdapter(getActivity(), mWorkoutList, mListener);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoggedWeightWorkoutsListFragmentInteractionListener) {
            mListener = (OnLoggedWeightWorkoutsListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoggedWeightWorkoutsListFragmentInteractionListener");
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
    public interface OnLoggedWeightWorkoutsListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onViewLoggedWeightWorkoutsListFragmentInteraction(WeightWorkout workout);
    }

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

            mWorkoutList = new ArrayList<>();
            result = WeightWorkout.parseWeightWorkoutJSON(result, mWorkoutList);
            // Something wrong with the JSON returned.
            if (result != null) {
                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
                        .show();
                return;
            }

            // Everything is good, show the list of courses.
            if (!mWorkoutList.isEmpty()) {
//                mRecyclerView.setAdapter(new MyViewLoggedWorkoutsRecyclerViewAdapter(mWorkoutList, mListener));
                mAdapter = new ViewLoggedWorkoutsAdapter(getActivity(), mWorkoutList, mListener);
                ListView view = (ListView) getActivity().findViewById(R.id.logged_workouts_list);
                view.setAdapter(mAdapter);
            }
        }
    }
}
